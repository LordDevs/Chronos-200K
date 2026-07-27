/**
 * CHRONOS voice channel — Web Speech API (browser-only).
 */

/** Normalize pt-PT STT quirks before AIML routing. */
window.normalizeVoiceTranscript = function normalizeVoiceTranscript(raw) {
  let text = (raw || "").trim().replace(/\s+/g, " ");
  if (!text) return text;

  const key = text
    .toLowerCase()
    .normalize("NFD")
    .replace(/\p{M}/gu, "")
    .replace(/[.,!?;:]/g, "")
    .trim();

  const exact = {
    "ola": "OLA",
    "ola chronos": "OLA CHRONOS",
    "bom dia": "BOM DIA",
    "status da nave": "STATUS DA NAVE",
    "estado da nave": "ESTADO DA NAVE",
    "telemetria": "TELEMETRIA",
    "verificar oxigenio": "VERIFICAR OXIGENIO",
    "niveis de oxigenio": "NIVEIS DE OXIGENIO",
    "analisar planeta oceano": "ANALISAR PLANETA OCEANO",
    "analisar o planeta oceano": "ANALISAR PLANETA OCEANO",
    "analisar planeta selva": "ANALISAR PLANETA SELVA",
    "analisar o planeta selva": "ANALISAR PLANETA SELVA",
    "ativar kit astartes": "ATIVAR KIT ASTARTES",
    "ativar astartes kit": "ATIVAR ASTARTES KIT",
    "prever evolucao 200 mil": "PREVER EVOLUCAO 200 MIL",
    "prever evolucao 200k": "PREVER EVOLUCAO 200K",
    "evolucao 200 mil anos": "EVOLUCAO 200 MIL ANOS",
    "ajuda": "AJUDA",
    "quem es tu": "QUEM ES TU",
  };

  if (exact[key]) return exact[key];

  text = text.replace(/\b200\s*mil\b/gi, "200K");
  text = text.replace(/\bduzentos\s*mil\b/gi, "200K");

  // STT often drops hyphens in exoplanet names: "kepler 442 b" → "kepler-442 b"
  text = text.replace(/\bkepler\s+(\d+)\s+([a-z])\b/gi, "kepler-$1 $2");
  text = text.replace(/\bproxima\s+centauri\s+([a-z])\b/gi, "proxima centauri $1");
  text = text.replace(/\bk2\s+18\s+([a-z])\b/gi, "k2-18 $1");

  return text;
};

window.createVoiceChannel = function createVoiceChannel({ onTranscript, onStatus, onError }) {
  const SpeechRecognition =
    window.SpeechRecognition || window.webkitSpeechRecognition;

  const supported = Boolean(SpeechRecognition) && "speechSynthesis" in window;

  let recognition = null;
  let listening = false;
  let ttsEnabled = true;

  function setStatus(msg) {
    if (onStatus) onStatus(msg);
  }

  function stripHtml(html) {
    const el = document.createElement("div");
    el.innerHTML = html;
    return (el.textContent || "").replace(/\s+/g, " ").trim();
  }

  function speak(text) {
    if (!ttsEnabled || !text) return;
    window.speechSynthesis.cancel();
    const utter = new SpeechSynthesisUtterance(text.slice(0, 600));
    utter.lang = "pt-PT";
    utter.rate = 0.95;
    utter.pitch = 0.9;
    const voices = window.speechSynthesis.getVoices();
    const ptVoice = voices.find((v) => v.lang.startsWith("pt"));
    if (ptVoice) utter.voice = ptVoice;
    window.speechSynthesis.speak(utter);
  }

  function ensureRecognition() {
    if (!SpeechRecognition) return null;
    if (recognition) return recognition;

    recognition = new SpeechRecognition();
    recognition.lang = "pt-PT";
    recognition.interimResults = false;
    recognition.continuous = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = () => {
      listening = true;
      setStatus("VOZ // a ouvir…");
    };

    recognition.onend = () => {
      listening = false;
      setStatus("VOZ // standby");
    };

    recognition.onerror = (ev) => {
      listening = false;
      const msg =
        ev.error === "not-allowed"
          ? "Microfone bloqueado. Permita o acesso no browser."
          : `Erro de voz: ${ev.error}`;
      setStatus("VOZ // erro");
      if (onError) onError(msg);
    };

    recognition.onresult = (ev) => {
      const transcript = ev.results[0][0].transcript.trim();
      if (transcript && onTranscript) {
        onTranscript(window.normalizeVoiceTranscript(transcript));
      }
    };

    return recognition;
  }

  return {
    isSupported: () => supported,

    isListening: () => listening,

    isTtsEnabled: () => ttsEnabled,

    toggleTts() {
      ttsEnabled = !ttsEnabled;
      if (!ttsEnabled) window.speechSynthesis.cancel();
      return ttsEnabled;
    },

    toggleListen() {
      const rec = ensureRecognition();
      if (!rec) {
        if (onError) {
          onError("Reconhecimento de voz não suportado neste browser. Use Chrome ou Edge.");
        }
        return false;
      }
      if (listening) {
        rec.stop();
        return false;
      }
      try {
        rec.start();
        return true;
      } catch (e) {
        if (onError) onError("Não foi possível iniciar o microfone.");
        return false;
      }
    },

    stopListen() {
      if (recognition && listening) recognition.stop();
    },

    speakPlain(htmlOrText) {
      speak(stripHtml(htmlOrText));
    },

    stopSpeak() {
      window.speechSynthesis.cancel();
    },
  };
};
