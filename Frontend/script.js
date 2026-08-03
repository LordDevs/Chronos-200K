(() => {
  const apiUrl = `${window.location.origin}/api`;
  const messagesEl = document.getElementById("chat-messages");
  const form = document.getElementById("chatForm");
  const input = document.getElementById("chatInput");
  const linkStatus = document.getElementById("linkStatus");
  const planetCards = document.getElementById("planetCards");
  const colonyCards = document.getElementById("colonyCards");
  const colonyForm = document.getElementById("colonyForm");
  const quickReplies = document.getElementById("quickReplies");
  const voiceBar = document.getElementById("voiceBar");
  const micBtn = document.getElementById("micBtn");
  const ttsBtn = document.getElementById("ttsBtn");
  const langBtn = document.getElementById("langBtn");
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
      if (
        lower.includes("planet")
        || lower.startsWith("exoplanet:")
        || lower.startsWith("analyze")
        || lower.startsWith("deep scan")
        || lower.startsWith("deepscan:")
        || lower.startsWith("compare")
        || lower.includes("vault")
      ) {
        upsertPlanetCard(message, reply);
      }
      if (lower.includes("learning mode") || lower.includes("colony archive") || reply.includes("COLONY ARCHIVE")) {
        refreshColonyCards();
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

  function buildColonySaveCommand() {
    const name = document.getElementById("colonyName").value.trim();
    const gravity = document.getElementById("colonyGravity").value;
    const water = document.getElementById("colonyWater").value;
    const temp = document.getElementById("colonyTemp").value;
    const gens = document.getElementById("colonyGens").value;
    const atmo = document.getElementById("colonyAtmo").value;
    const atmoToken = atmo.toLowerCase().includes("co2") ? "co2"
      : atmo.toLowerCase().includes("ch4") ? "ch4"
      : atmo.toLowerCase().includes("thin") ? "thin"
      : "";
    const atmoPart = atmoToken ? ` ${atmoToken}` : "";
    return `LEARNING MODE SAVE ${name} gravity ${gravity}g water ${water} temp ${temp}${atmoPart} generations ${gens}`;
  }

  function formatColonyMeta(c) {
    return `g=${c.gravityG} · água=${c.waterPercent}% · ${c.temperatureC}°C · ${c.atmosphere} · ${c.generations} gen.`;
  }

  async function refreshColonyCards() {
    if (!colonyCards) return;
    try {
      const res = await fetch(`${window.location.origin}/api/colonies`);
      if (!res.ok) throw new Error("HTTP " + res.status);
      const colonies = await res.json();
      colonyCards.innerHTML = "";
      if (!colonies.length) {
        colonyCards.innerHTML = '<p class="colony-empty">Nenhum perfil guardado. Use o formulário ou LEARNING MODE SAVE.</p>';
        return;
      }
      colonies.forEach((c) => {
        const card = document.createElement("article");
        card.className = "colony-card";
        card.dataset.colony = c.name;
        card.innerHTML = `
          <h3>${c.name}</h3>
          <div class="meta">${formatColonyMeta(c)}</div>
          <div class="actions">
            <button type="button" data-evolve="${c.name}">Evoluir</button>
            <button type="button" data-load="${c.name}">Carregar</button>
            <button type="button" data-delete="${c.name}">Apagar</button>
          </div>
        `;
        colonyCards.appendChild(card);
      });
    } catch (err) {
      console.warn("Colony refresh failed", err);
    }
  }

  colonyCards?.addEventListener("click", (e) => {
    const evolve = e.target.closest("[data-evolve]");
    const load = e.target.closest("[data-load]");
    const del = e.target.closest("[data-delete]");
    if (evolve) sendToChronos(`LEARNING MODE EVOLVE ${evolve.dataset.evolve}`);
    if (load) sendToChronos(`LEARNING MODE LOAD ${load.dataset.load}`);
    if (del) sendToChronos(`LEARNING MODE DELETE ${del.dataset.delete}`);
  });

  colonyForm?.addEventListener("submit", (e) => {
    e.preventDefault();
    sendToChronos(buildColonySaveCommand());
  });

  function syncLangButton() {
    if (!langBtn) return;
    const label = voice.getLangLabel();
    langBtn.textContent = label;
    langBtn.title =
      label === "PT"
        ? "Idioma de voz: Português (clique para EN)"
        : "Voice language: English (click for PT)";
    langBtn.setAttribute("aria-pressed", label === "EN" ? "true" : "false");
    langBtn.classList.toggle("active", label === "EN");
  }

  function setupVoiceUi() {
    if (!voice.isSupported()) {
      if (voiceBar) voiceBar.classList.add("voice-unsupported");
      if (voiceStatus) voiceStatus.textContent = "VOZ // não suportada (use Chrome/Edge)";
      if (micBtn) micBtn.disabled = true;
      if (langBtn) langBtn.disabled = true;
      return;
    }

    syncLangButton();

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

    langBtn?.addEventListener("click", () => {
      voice.stopListen();
      document.body.classList.remove("helmet-listening");
      micBtn?.classList.remove("active");
      micBtn?.setAttribute("aria-pressed", "false");
      voice.toggleLang();
      syncLangButton();
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
  refreshColonyCards();

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
