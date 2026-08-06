#!/usr/bin/env python3
import os
import sys
import subprocess
import requests

def run_command(cmd):
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return result.stdout.strip()

def get_git_diff(event_name, before_sha):
    if event_name == 'pull_request':
        # Diff against base branch
        return run_command("git diff origin/main...HEAD") or run_command("git diff HEAD~1 HEAD")
    else:
        if before_sha and before_sha != "0000000000000000000000000000000000000000":
            return run_command(f"git diff {before_sha} HEAD")
        return run_command("git diff HEAD~1 HEAD")

def read_skill_rules():
    skill_path = os.path.join(".agents", "skills", "automation-code-review", "SKILL.md")
    if os.path.exists(skill_path):
        with open(skill_path, "r", encoding="utf-8") as f:
            return f.read()
    return "Enforce API BDD best practices, thread safety, external payloads, and JSON schema validations."

def call_gemini_api(prompt, api_key):
    # Using Gemini REST API with key in header for security
    url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
    headers = {
        "Content-Type": "application/json",
        "x-goog-api-key": api_key
    }
    payload = {
        "contents": [
            {
                "parts": [
                    {"text": prompt}
                ]
            }
        ]
    }
    
    response = requests.post(url, headers=headers, json=payload, timeout=60)
    if response.status_code != 200:
        # Fallback to gemini-1.5-flash if 2.0-flash model name differs
        url_fallback = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
        response = requests.post(url_fallback, headers=headers, json=payload, timeout=60)
        
    if response.status_code == 200:
        data = response.json()
        try:
            return data["candidates"][0]["content"]["parts"][0]["text"]
        except (KeyError, IndexError):
            return "Error parsing Gemini response payload."
    else:
        return f"Gemini API Call Failed (Status {response.status_code})"

def post_github_comment(review_text, github_token, repository, event_name, pr_number, commit_sha):
    headers = {
        "Authorization": f"Bearer {github_token}",
        "Accept": "application/vnd.github+json"
    }

    footer = "\n\n---\n*🤖 Automated Code Review by Gemini AI Agent*"
    full_comment = review_text + footer

    if event_name == "pull_request" and pr_number:
        url = f"https://api.github.com/repos/{repository}/issues/{pr_number}/comments"
    else:
        url = f"https://api.github.com/repos/{repository}/commits/{commit_sha}/comments"

    res = requests.post(url, headers=headers, json={"body": full_comment}, timeout=30)
    if res.status_code in [200, 201]:
        print("Successfully posted AI review comment to GitHub!")
    else:
        print(f"Failed to post comment to GitHub: {res.status_code} - {res.text}")

def main():
    api_key = os.getenv("GEMINI_API_KEY")
    github_token = os.getenv("GITHUB_TOKEN")
    repository = os.getenv("GITHUB_REPOSITORY")
    event_name = os.getenv("EVENT_NAME", "push")
    commit_sha = os.getenv("COMMIT_SHA")
    pr_number = os.getenv("PR_NUMBER")
    before_sha = os.getenv("BEFORE_SHA")

    if not api_key:
        print("GEMINI_API_KEY environment variable is missing. Skipping AI code review.")
        sys.exit(0)

    diff = get_git_diff(event_name, before_sha)
    if not diff or diff.strip() == "":
        print("No code changes detected in this commit/PR. Skipping review.")
        sys.exit(0)

    # Truncate giant diffs if necessary to fit context window
    if len(diff) > 40000:
        diff = diff[:40000] + "\n...[Diff truncated due to size]..."

    rules = read_skill_rules()

    prompt = f"""You are an expert API Test Automation Architect performing an automated code review on a GitHub commit/PR.

### CODE REVIEW RULES & GUIDELINES:
{rules}

### CODE CHANGES (GIT DIFF):
```diff
{diff}
```

Evaluate the code changes against the rules above. Provide a clear, professional, structured Markdown review.
"""

    print("Sending diff to Gemini AI model for review...")
    review = call_gemini_api(prompt, api_key)

    print("Posting review output to GitHub...")
    post_github_comment(review, github_token, repository, event_name, pr_number, commit_sha)

if __name__ == "__main__":
    main()
