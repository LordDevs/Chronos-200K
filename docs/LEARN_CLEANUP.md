# LEARN cleanup (apply manually)

This cloud agent can push to **Chronos-200K** but not to **LEARN** (403 for `cursor[bot]`).

The cleanup commit was prepared locally. Apply it on a machine authenticated as **LordDevs**:

```bash
git clone --branch LordDevs https://github.com/LordDevs/LEARN.git
cd LEARN
git checkout -b cursor/remove-chatbot-pointer-33b7

# Remove legacy chatbot code; keep pointer stub
rm -rf chatbot/Backend chatbot/Frontend chatbot/run.bat chatbot/run.sh

cat > chatbot/README.md << 'STUB'
# Chatbot (moved)

The chatbot that lived in this folder has been migrated and remodeled as **Chronos-200K**.

**Canonical repository:** https://github.com/LordDevs/Chronos-200K

This directory is kept only as a pointer so old links do not look like an active duplicate codebase. Historical commits under `chatbot/` remain in this repo’s Git history.
STUB

cat > README.md << 'ROOT'
# LEARN

Academic projects monorepo (HTML/CSS, Bootstrap, JavaScript, jQuery, XAMPP demos, etc.).

## Chronos-200K

The former `chatbot/` project was migrated out of this repository.

→ **https://github.com/LordDevs/Chronos-200K** (deep-time AI simulator for human evolution and exoplanetary adaptation)

See [`chatbot/README.md`](chatbot/README.md) for the pointer stub.
ROOT

git add -A
git commit -m "Remove legacy chatbot codebase; point to Chronos-200K."
git push -u origin cursor/remove-chatbot-pointer-33b7
# Open PR into default branch LordDevs
```

Or apply the patch committed beside this file: `docs/learn-cleanup.patch` (generated from the prepared commit).
