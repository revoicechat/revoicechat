# 🌍 ReVoiceChat — Translations

Thank you for helping translate ReVoiceChat! This is where all translations live.  
**No coding skills needed** — if you speak another language, you can contribute.

---

## 🌐 Translation Progress

<!-- PROGRESS_START -->
| Language | admin-dashboard | error | frontend | login | risks | server-settings | user-settings |
|---|---|---|---|---|---|---|---|
| `en` | ✅ 100% | ✅ 100% | ✅ 100% | ✅ 100% | ✅ 100% | ✅ 100% | ✅ 100% |
| `fr` | ✅ 100% | 🟡 96% | 🟡 84% | ✅ 100% | ✅ 100% | 🟡 91% | 🟡 91% |
| `it` | ❌ 0% | ❌ 0% | 🟡 77% | 🟡 82% | ❌ 0% | 🟡 89% | 🟡 67% |

<!-- PROGRESS_END -->

Missing your language? You can add it! See below.

---

## ✏️ How to Translate

### 1 — Find the files to translate

Translations are split into different folders, one per type of strings:

| Folder             | What it covers                                                                |
|--------------------|-------------------------------------------------------------------------------|
| `frontend/`        | Everything you see in the app (buttons, messages, warnings…)                  |
| `user-settings/`   | Everything you see in the server settings (emote, roles, structure…)          |
| `server-settings/` | Everything you see in the user settings (appareance, profile…)                |
| `risks/`           | Every risks/authorization of the application (voice, room, message…)          |
| `error/`           | Every errors the backend can return (object does not exists, internal error…) |

Each folder contains one file per language, named with its language code:  

    - `en.properties` → English,
    - `fr.properties` → French,
    - `es.properties` → Spanish
    - …

### 2 — Edit or create your language file

**To improve an existing translation**, open the file for your language (e.g. `frontend/fr.properties`) and find the lines still in English.

**To start a new language**, duplicate the English file and rename it:

```
frontend/en.properties  →  frontend/de.properties   (for German)
frontend/en.properties  →  frontend/pt.properties   (for Portuguese)
```

> Language codes follow the [ISO 639-1 standard](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes): `fr`, `es`, `de`, `pt`, `ja`, `zh`…

> You can also generate all the files empty (with just the keys) using `./.scripts/.scripts/new-lang.sh ${lang}` 

### 3 — Translate the file

Open your `.properties` file in any text editor. Each line looks like this:

```properties
button.connect=Connect
app.welcome=Welcome to ReVoiceChat
error.disconnected=You have been disconnected
```

**Translate only the part after the `=` sign.** Never change what's before it.

```properties
# ✅ Correct
button.connect=Connexion
app.welcome=Bienvenue sur ReVoiceChat

# ❌ Wrong — the key (left side) was changed
bouton.connexion=Connexion
```

#### ⚠️ Important rules

- **Keep placeholders as-is** — things like `{username}`, `{0}`, `%s` are filled in automatically by the app. Do not translate or remove them.
  ```properties
  # ✅ Correct
  message.welcome=Bienvenue, {username} !
  # ❌ Wrong
  message.welcome=Bienvenue, nom_utilisateur !
  ```
- **Keep every line** — even if you're unsure how to translate something, leave it in English rather than deleting it.
- **Not sure about a word?** Leave a comment on your Pull Request and a maintainer will help.

### 4 — Submit your translation

1. Go to the file you edited on GitHub
2. Scroll down and click **"Propose changes"**
3. Open a **Pull Request** with a title like: `Add German translation (frontend)`

That's it! A maintainer will review and merge it. Your translation will automatically be deployed to the app. 🎉

---

## 💬 Need help?

- **Not sure about a translation?** Open a Pull Request anyway and ask in the description — the community will help.
- **Found a mistake in an existing translation?** Open an issue or directly fix it with a PR.
- **Want to discuss wording?** Open an issue with the `translation` label.

---

## 🔗 Useful links

- [ISO 639-1 language codes](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)
- [ReVoiceChat main project](https://github.com/revoicechat)
- [How to fork and edit a file on GitHub (no git required)](https://docs.github.com/en/repositories/working-with-files/managing-files/editing-files)