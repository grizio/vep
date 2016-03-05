(function () {
  class ContentType {
    //type
    //subtype

    constructor(type, subtype) {
      this.type = type;
      this.subtype = subtype;
      Object.freeze(this);
    }

    get isGeneric() {
      return this.subtype === "*";
    }

    get isNone() {
      return this.type == null && this.subtype == null;
    }

    get isNotNone() {
      return !this.isNone;
    }

    equals(other) {
      return other instanceof ContentType && other.type === this.type && other.subtype === this.subtype;
    }

    acceptedBy(other) {
      return other instanceof ContentType && other.type === this.type && (other.subtype === "*" || other.subtype === this.subtype);
    }

    toString() {
      return `${this.type}/${this.subtype}`;
    }
  }

  function parse(str) {
    const parts = str.split("/");
    return new ContentType(parts[0], parts[1]);
  }

  window.vep = window.vep || {};
  window.vep.ContentType = ContentType;
  window.vep.ContentTypes = Object.freeze({
    Application: new ContentType("application", "*"),
    ApplicationJavascript: new ContentType("application", "javascript"),
    ApplicationJson: new ContentType("application", "json"),
    ApplicationXml: new ContentType("application", "xml"),

    Audio: new ContentType("audio", "*"),

    ImageGif: new ContentType("image", "gif"),
    ImageJpeg: new ContentType("image", "jpeg"),
    ImagePng: new ContentType("image", "png"),
    ImageXIcon: new ContentType("image", "x-icon"),

    Text: new ContentType("text", "*"),
    TextCss: new ContentType("text", "css"),
    TextCsv: new ContentType("text", "csv"),
    TextHtml: new ContentType("text", "html"),
    TextPlain: new ContentType("text", "plain"),
    TextXml: new ContentType("text", "xml"),

    Video: new ContentType("video", "*"),
    VideoMpeg: new ContentType("video", "mpeg"),
    VideoMp4: new ContentType("video", "mp4"),

    None: new ContentType(null, null),

    parse: parse
  });
})();