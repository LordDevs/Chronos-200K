/**
 * CHRONOS voice channel — Web Speech API (browser-only).
 */
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
        onTranscript(transcript);
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
