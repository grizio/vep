(() => {
  class StringUtils {
    isEmpty(str) {
      return !str || str === "";
    }

    canonicalize(str) {
      if (this.isEmpty(str)) {
        return "";
      } else {
        str = str.toLowerCase();
        str = str.replace(/(à|ä|â|@)/gi, "a");
        str = str.replace(/(é|è|ë|ê)/gi, "e");
        str = str.replace(/(ï|î)/gi, "i");
        str = str.replace(/(ö|ô)/gi, "o");
        str = str.replace(/(ù|ü|û)/gi, "u");
        str = str.replace(/\\s+/gi, "-");
        str = str.replace(/[^a-z0-9_+-]+/gi, "-");
        str = str.replace(/-+/gi, "-");
        const chars = ["_", "-", "+"];
        while (chars.some((c) => str.startsWith(c))) {
          str = str.substring(1);
        }
        while (chars.some((c) => str.endsWith(c))) {
          str = str.substring(0, str.length - 1);
        }
        return str;
      }
    }

    equals(str1, str2) {
      return str1 === null && str2 === null || str1 === str2;
    }
  }

  window.vep = window.vep || {};
  window.vep.utils = window.vep.utils || {};
  window.vep.utils.string = Object.freeze(new StringUtils());
})();