#!/bin/bash
# ============================================================
#  GIT COMMANDS CHEAT SHEET
#  Quick reference — setup to advanced
# ============================================================


# ============================================================
#  1. SETUP & CONFIG
# ============================================================

git config --global user.name "Your Name"        # set your name for all commits on this machine
git config --global user.email "you@email.com"   # set your email for all commits on this machine
git config --global core.editor "code --wait"    # set VS Code as the default editor
git config --list                                 # list all current config settings


# ============================================================
#  2. STARTING A REPO
# ============================================================

git init                       # initialize a new local Git repository in the current folder
git clone <url>                # download an existing remote repository to your local machine
git clone <url> my-folder      # clone into a specific folder name instead of the default


# ============================================================
#  3. STAGING & COMMITTING
# ============================================================

git status                     # show which files are modified, staged, or untracked
git add <file>                 # stage a specific file for the next commit
git add .                      # stage all changed and new files in the current directory
git add -p                     # interactively choose which changes (hunks) to stage

git commit -m "message"        # create a commit with a short inline message
git commit --amend             # rewrite the most recent commit (message or staged files); never do this on pushed commits

git diff                       # show unstaged changes (what changed but not yet staged)
git diff --staged              # show staged changes (what will go into the next commit)


# ============================================================
#  4. BRANCHES
# ============================================================

git branch                     # list all local branches (* marks the current one)
git branch -a                  # list both local and remote-tracking branches
git branch <name>              # create a new branch at the current commit
git branch -d <name>           # delete a branch (safe — blocked if unmerged changes exist)
git branch -D <name>           # force-delete a branch regardless of merge status

git switch <name>              # switch to an existing branch (modern alternative to checkout)
git switch -c <name>           # create and switch to a new branch in one step
git checkout <name>            # switch branches (older syntax, still widely used)
git checkout -b <name>         # create and switch to a new branch (older syntax)

git merge <branch>             # merge the specified branch into the current branch
git merge --no-ff <branch>     # merge with a merge commit even if fast-forward is possible (preserves history)
git merge --abort              # cancel an in-progress merge and restore the pre-merge state

git rebase <branch>            # replay current branch commits on top of another branch (linear history)
git rebase --abort             # cancel an in-progress rebase
git rebase -i HEAD~3           # interactive rebase: squash, reorder, or edit the last 3 commits


# ============================================================
#  5. REMOTE REPOSITORIES
# ============================================================

git remote -v                          # list all configured remotes with their URLs
git remote add origin <url>            # link your local repo to a remote called "origin"
git remote set-url origin <url>        # change the URL of an existing remote

git fetch                              # download changes from remote without merging them
git fetch --prune                      # fetch + delete local tracking branches that no longer exist on remote

git pull                               # fetch + merge remote changes into the current branch
git pull --rebase                      # fetch + rebase instead of merge (cleaner linear history)

git push origin <branch>               # push a local branch to the remote
git push -u origin <branch>            # push and set the upstream so future `git push` works without arguments
git push --force-with-lease            # force push but only if nobody else has pushed since your last fetch (safer than --force)
git push origin --delete <branch>      # delete a branch on the remote


# ============================================================
#  6. UNDOING CHANGES
# ============================================================

git restore <file>             # discard unstaged changes in a file (replaces old `git checkout -- <file>`)
git restore --staged <file>    # unstage a file without losing the changes (move it back to working tree)

git revert <commit>            # create a new commit that undoes a specific commit (safe for shared history)
git reset --soft HEAD~1        # undo the last commit but keep changes staged
git reset --mixed HEAD~1       # undo the last commit and unstage changes (default reset behavior)
git reset --hard HEAD~1        # undo the last commit and permanently discard all changes — DESTRUCTIVE

git clean -fd                  # delete all untracked files and directories — DESTRUCTIVE


# ============================================================
#  7. VIEWING HISTORY
# ============================================================

git log                        # full commit history with author, date, and message
git log --oneline              # compact one-line-per-commit view
git log --oneline --graph      # ASCII graph showing branch and merge topology
git log --oneline -10          # show only the last 10 commits
git log --author="Name"        # filter commits by author name
git log -- <file>              # show history of changes to a specific file

git show <commit>              # show the diff and metadata of a specific commit
git blame <file>               # show which commit and author last changed each line of a file


# ============================================================
#  8. STASHING
# ============================================================

git stash                      # temporarily save uncommitted changes and revert the working tree to HEAD
git stash push -m "label"      # stash with a descriptive name so you can find it later
git stash list                 # list all saved stashes
git stash pop                  # apply the most recent stash and remove it from the stash list
git stash apply stash@{2}      # apply a specific stash without removing it from the list
git stash drop stash@{0}       # delete a specific stash entry
git stash clear                # delete all stashes


# ============================================================
#  9. TAGS
# ============================================================

git tag                        # list all tags
git tag v1.0.0                 # create a lightweight tag at the current commit
git tag -a v1.0.0 -m "msg"    # create an annotated tag with a message (recommended for releases)
git push origin v1.0.0         # push a specific tag to the remote
git push origin --tags         # push all local tags to the remote
git tag -d v1.0.0              # delete a tag locally


# ============================================================
#  10. USEFUL SHORTCUTS
# ============================================================

git shortlog -sn               # summarize commit count per author, sorted by most commits
git cherry-pick <commit>       # apply a specific commit from another branch onto the current branch
git bisect start               # start a binary search to find which commit introduced a bug
git reflog                     # show a log of every HEAD movement — lifesaver for recovering lost commits


# ============================================================
#  QUICK REFERENCE — EVERYDAY WORKFLOW
# ============================================================
#
#  Start work:
#    git switch -c feature/my-feature        # new branch
#    git pull --rebase origin main           # get latest main
#
#  During work:
#    git status                              # check what changed
#    git add .  &&  git commit -m "..."      # stage and commit
#    git stash                               # save work temporarily if you need to switch context
#
#  Before merging:
#    git fetch --prune                       # sync with remote
#    git rebase origin/main                  # put your commits on top of latest main
#
#  After merging:
#    git branch -d feature/my-feature        # clean up local branch
#    git push origin --delete feature/my-feature  # clean up remote branch
#
#  Oops, I committed to main directly:
#    git branch feature/rescue               # save the work on a new branch
#    git reset --hard origin/main            # put main back to remote state
