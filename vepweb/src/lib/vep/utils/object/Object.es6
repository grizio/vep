(function () {
  function clone(obj) {
    if (obj instanceof Array) {
      return obj.map((_) => clone(_));
    } else if (obj instanceof Object) {
      if (Object.isFrozen(obj)) {
        return obj;
      } else if (obj.processClone && obj.processClone instanceof Function) {
        return obj.processClone();
      } else {
        const result = {};
        foreachKeyValue(obj, (key, value) => result[key] = clone(value));
        return result;
      }
    } else {
      return obj;
    }
  }

  function merge(obj1, obj2) {
    const result = {};
    foreachKeyValue(obj2, (key, value) => result[key] = value);
    foreachKeyValue(obj1, (key, value) => result[key] = value);
    return result;
  }

  function foreachKeyValue(obj, callback) {
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        callback(key, obj[key]);
      }
    }
  }

  window.vep = window.vep || {};
  window.vep.utils = window.vep.utils || {};
  window.vep.utils.object = Object.freeze({
    clone: clone,
    merge: merge,
    foreachKeyValue: foreachKeyValue
  });
})();