# Masolo — application Android

Application native (Kotlin) qui affiche votre serveur Masolo dans une WebView, avec : envoi de photos / vidéos / documents,
téléchargement des documents reçus, boîtes de dialogue (confirmations, signalements), bouton retour, liens externes dans le navigateur,
page « hors connexion », session conservée entre deux ouvertures. Android 8.0 (API 26) et plus.

> L'application a besoin d'un **serveur Masolo accessible depuis le téléphone**. Elle n'embarque pas PHP/MySQL.

## 1. Tester rapidement (sans serveur public)
1. Sur le PC : Apache + MySQL démarrés, Masolo installé (`http://localhost/masolo/`), `php websocket/server.php` lancé si vous voulez le temps réel.
2. `gradle.properties` :
   - émulateur Android Studio : `MASOLO_DEBUG_URL=http://10.0.2.2/masolo/`
   - vrai téléphone (même Wi-Fi que le PC) : `MASOLO_DEBUG_URL=http://192.168.X.X/masolo/` (IP du PC ; autoriser Apache dans le pare-feu Windows, port 80 et 8090).
3. Android Studio (Hedgehog ou plus récent) → **Open** → dossier `masolo-android` → attendre la synchronisation → ▶ **Run**.
4. APK de débogage : `app/build/outputs/apk/debug/app-debug.apk` (menu *Build > Build Bundle(s) / APK(s) > Build APK(s)*).
   Sur le téléphone : activer « Sources inconnues » puis ouvrir le fichier.

## 2. Version de production (APK à distribuer)
1. Déployer Masolo sur un serveur avec **HTTPS** (voir `deploy/` dans le projet web : Apache, systemd pour le WebSocket).
   Dans le `.env` du serveur : `APP_ENV=production`, `APP_DEBUG=false`, `APP_KEY=…`, `WS_PUBLIC_URL=wss://votre-domaine/ws`.
2. `gradle.properties` : `MASOLO_PROD_URL=https://votre-domaine/`
3. Créer une clé de signature (à conserver précieusement, elle est nécessaire pour toutes les mises à jour) :
   `keytool -genkey -v -keystore masolo.jks -keyalg RSA -keysize 2048 -validity 10000 -alias masolo`
   puis décommenter les 4 lignes `MASOLO_KEYSTORE…` de `gradle.properties`.
4. *Build > Generate Signed Bundle / APK* (ou `gradle assembleRelease`) → `app/build/outputs/apk/release/app-release.apk`.
   Pour le Play Store : choisir **Android App Bundle (.aab)**.
5. À chaque nouvelle version : augmenter `versionCode` dans `app/build.gradle.kts`.

## 3. Sans Android Studio : compilation dans le cloud
Déposer ce dossier dans un dépôt GitHub → onglet **Actions** → *Build APK* → *Run workflow* → télécharger l'artefact `masolo-apk`.

## Limites actuelles
- Pas de notifications push quand l'application est fermée (il faut Firebase Cloud Messaging + une étape serveur).
- Le temps réel (messages instantanés) fonctionne application ouverte ; sinon les messages arrivent à la réouverture.
- Avec une adresse HTTPS, le WebSocket doit être en `wss://` (sinon le navigateur intégré le bloque).
