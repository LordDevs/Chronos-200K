(() => {
  const apiUrl = `${window.location.origin}/api`;
  const messagesEl = document.getElementById("chat-messages");
  const form = document.getElementById("chatForm");
  const input = document.getElementById("chatInput");
  const linkStatus = document.getElementById("linkStatus");
  const planetCards = document.getElementById("planetCards");
  const quickReplies = document.getElementById("quickReplies");
  const voiceBar = document.getElementById("voiceBar");
  const micBtn = document.getElementById("micBtn");
  const ttsBtn = document.getElementById("ttsBtn");
  const voiceStatus = document.getElementById("voiceStatus");

  const seedPlanets = ["kepler-442 b", "proxima centauri b", "k2-18 b"];
  const FETCH_MS = 15000;

  const voice = window.createVoiceChannel({
    onTranscript: (text) => {
      input.value = text;
      sendToChronos(text);
    },
    onStatus: (msg) => {
      if (voiceStatus) voiceStatus.textContent = msg;
    },
    onError: (msg) => appendMessage(msg, "bot"),
  });

  function appendMessage(html, sender) {
    const div = document.createElement("div");
    div.className = `message ${sender}`;
    if (sender === "bot") {
      div.innerHTML = html;
    } else {
      div.textContent = html;
    }
    messagesEl.appendChild(div);
    messagesEl.scrollTop = messagesEl.scrollHeight;
    return div;
  }

  function showTyping() {
    const typing = document.createElement("div");
    typing.className = "message bot typing-indicator";
    typing.innerHTML = "<span></span><span></span><span></span>";
    messagesEl.appendChild(typing);
    messagesEl.scrollTop = messagesEl.scrollHeight;
    return typing;
  }

  async function postChronos(message) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), FETCH_MS);
    try {
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: { "Content-Type": "text/plain;charset=UTF-8" },
        body: message,
        signal: controller.signal,
      });
      if (!response.ok) {
        throw new Error("HTTP " + response.status);
      }
      return await response.text();
    } finally {
      clearTimeout(timer);
    }
  }

  async function sendToChronos(text, { showUser = true } = {}) {
    const message = text.trim();
    if (!message) return;

    if (showUser) {
      appendMessage(message, "user");
    }
    const typing = showTyping();

    try {
      const reply = await postChronos(message);
      typing.remove();
      linkStatus.classList.add("online");
      appendMessage(reply, "bot");
      voice.speakPlain(reply);

      const lower = message.toLowerCase();
      if (lower.includes("planet") || lower.startsWith("exoplanet:") || lower.startsWith("analyze")) {
        upsertPlanetCard(message, reply);
      }
    } catch (err) {
      typing.remove();
      linkStatus.classList.remove("online");
      appendMessage("Link perdido com CHRONOS (timeout/rede). Confirme run.bat em :8080.", "bot");
      console.error(err);
    }
  }

  function upsertPlanetCard(query, rawHtml) {
    const titleMatch = rawHtml.match(/<strong>(.*?)<\/strong>/i);
    const name = titleMatch
      ? titleMatch[1]
      : query.replace(/analyze planet/i, "").replace(/exoplanet:/i, "").trim();
    const habitable = /zona habitável/i.test(rawHtml);
    const hostile = /demasiado|fora da zh/i.test(rawHtml);

    let card = [...planetCards.querySelectorAll(".planet-card")]
      .find((el) => el.dataset.planet === name);
    if (!card) {
      card = document.createElement("article");
      card.className = "planet-card";
      card.dataset.planet = name;
      planetCards.prepend(card);
    }
    card.classList.toggle("habitable", habitable);
    card.classList.toggle("hostile", hostile && !habitable);
    card.innerHTML = `
      <h3>${name}</h3>
      <div class="meta">${new Date().toLocaleTimeString()} · NASA TAP</div>
      <div class="body">${rawHtml}</div>
    `;
  }

  function setupVoiceUi() {
    if (!voice.isSupported()) {
      if (voiceBar) voiceBar.classList.add("voice-unsupported");
      if (voiceStatus) voiceStatus.textContent = "VOZ // não suportada (use Chrome/Edge)";
      if (micBtn) micBtn.disabled = true;
      return;
    }

    micBtn?.addEventListener("click", () => {
      const on = voice.toggleListen();
      document.body.classList.toggle("helmet-listening", on);
      micBtn.classList.toggle("active", on);
      micBtn.setAttribute("aria-pressed", on ? "true" : "false");
    });

    ttsBtn?.addEventListener("click", () => {
      const on = voice.toggleTts();
      ttsBtn.classList.toggle("active", on);
      ttsBtn.setAttribute("aria-pressed", on ? "true" : "false");
      ttsBtn.title = on ? "Voz CHRONOS ligada" : "Voz CHRONOS desligada";
      if (!on) voice.stopSpeak();
    });

    if (ttsBtn) {
      ttsBtn.classList.add("active");
      ttsBtn.setAttribute("aria-pressed", "true");
    }

    // Chrome loads voices async
    window.speechSynthesis?.addEventListener("voiceschanged", () => {}, { once: true });
  }

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const value = input.value;
    input.value = "";
    voice.stopListen();
    document.body.classList.remove("helmet-listening");
    micBtn?.classList.remove("active");
    sendToChronos(value);
  });

  quickReplies.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-cmd]");
    if (!btn) return;
    sendToChronos(btn.dataset.cmd);
  });

  setupVoiceUi();

  appendMessage(
    "Sistema CHRONOS inicializado. Canal tático pronto. Use os atalhos, texto ou microfone (PT).",
    "bot"
  );
  linkStatus.classList.add("online");

  seedPlanets.forEach((planet, i) => {
    setTimeout(async () => {
      try {
        const reply = await postChronos(`exoplanet:${planet}`);
        upsertPlanetCard(planet, reply);
      } catch (err) {
        console.warn("Seed planet failed", planet, err);
      }
    }, 800 + i * 700);
  });
})();
