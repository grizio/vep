package vep.test.utils

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.utils.StringUtils

@RunWith(classOf[JUnitRunner])
class StringUtilsSpecification extends Specification {
  "Specifications of StringUtils functions" >> {
    "StringUtils.isEmail should" >> {
      "validate 'abc@abc.com'" >> {
        StringUtils.isEmail("abc@abc.com") === true
      }
      "validate 'abc.def@ghi.com'" >> {
        StringUtils.isEmail("abc.def@ghi.com") === true
      }
      "invalidate 'abc.def.com'" >> {
        StringUtils.isEmail("abc.def.com") === false
      }
    }

    "StringUtils.isNotEmail should" >> {
      "invalidate 'abc@abc.com'" >> {
        StringUtils.isNotEmail("abc@abc.com") === false
      }
      "invalidate 'abc.def@ghi.com'" >> {
        StringUtils.isNotEmail("abc.def@ghi.com") === false
      }
      "validate 'abc.def.com'" >> {
        StringUtils.isNotEmail("abc.def.com") === true
      }
    }

    "StringUtils.isEmpty should" >> {
      "validate ''" >> {
        StringUtils.isEmpty("") === true
      }
      "invalidate ' '" >> {
        StringUtils.isEmpty(" ") === false
      }
      "invalidate 'a'" >> {
        StringUtils.isEmpty("a") === false
      }
      "validate null" >> {
        StringUtils.isEmpty(null) === true
      }
    }

    "StringUtils.isNotEmpty should" >> {
      "invalidate ''" >> {
        StringUtils.isNotEmpty("") === false
      }
      "validate ' '" >> {
        StringUtils.isNotEmpty(" ") === true
      }
      "validate 'a'" >> {
        StringUtils.isNotEmpty("a") === true
      }
      "invalidate null" >> {
        StringUtils.isNotEmpty(null) === false
      }
    }

    "StringUtils.isBlank should" >> {
      "validate ''" >> {
        StringUtils.isBlank("") === true
      }
      "validate ' '" >> {
        StringUtils.isBlank(" ") === true
      }
      "invalidate 'a'" >> {
        StringUtils.isBlank("a") === false
      }
      "validate null" >> {
        StringUtils.isBlank(null) === true
      }
    }

    "StringUtils.isNotBlank should" >> {
      "invalidate ''" >> {
        StringUtils.isNotBlank("") === false
      }
      "invalidate ' '" >> {
        StringUtils.isNotBlank(" ") === false
      }
      "validate 'a'" >> {
        StringUtils.isNotBlank("a") === true
      }
      "invalidate null" >> {
        StringUtils.isNotBlank(null) === false
      }
    }

    "StringUtils.equals should" >> {
      "validate ('', '')" >> {
        StringUtils.equals("", "") === true
      }
      "validate (null, null)" >> {
        StringUtils.equals(null, null) === true
      }
      "validate ('a', 'a')" >> {
        StringUtils.equals("a", "a") === true
      }
      "invalidate (null, '')" >> {
        StringUtils.equals(null, "") === false
      }
      "invalidate ('', null)" >> {
        StringUtils.equals("", null) === false
      }
      "invalidate ('a', 'b')" >> {
        StringUtils.equals("a", "b") === false
      }
      "invalidate ('b', 'a')" >> {
        StringUtils.equals("b", "a") === false
      }
    }

    "StringUtils.equals should" >> {
      "invalidate ('', '')" >> {
        StringUtils.notEquals("", "") === false
      }
      "invalidate (null, null)" >> {
        StringUtils.notEquals(null, null) === false
      }
      "invalidate ('a', 'a')" >> {
        StringUtils.notEquals("a", "a") === false
      }
      "validate (null, '')" >> {
        StringUtils.notEquals(null, "") === true
      }
      "validate ('', null)" >> {
        StringUtils.notEquals("", null) === true
      }
      "validate ('a', 'b')" >> {
        StringUtils.notEquals("a", "b") === true
      }
      "validate ('b', 'a')" >> {
        StringUtils.notEquals("b", "a") === true
      }
    }

    "StringUtils.containsPattern should" >> {
      "validate ('abcde', 'c')" >> {
        StringUtils.containsPattern("abcde", "c") === true
      }
      "validate ('abcde', 'a')" >> {
        StringUtils.containsPattern("abcde", "a") === true
      }
      "validate ('abcde', 'e')" >> {
        StringUtils.containsPattern("abcde", "e") === true
      }
      "validate ('abcde012345, '[a-z][0-9]')" >> {
        StringUtils.containsPattern("abcde012345", "[a-z][0-9]") === true
      }
      "invalidate ('abcde', 'f')" >> {
        StringUtils.containsPattern("abcde", "f") === false
      }
      "invalidate ('abcde', '[0-9]')" >> {
        StringUtils.containsPattern("abcde", "[0-9]") === false
      }
      "invalidate ('abcdef012345', '[0-9][a-z]')" >> {
        StringUtils.containsPattern("abcde012345", "[0-9][a-z]") === false
      }
    }

    "StringUtils.containsNotPattern should" >> {
      "invalidate ('abcde', 'c')" >> {
        StringUtils.containsNotPattern("abcde", "c") === false
      }
      "invalidate ('abcde', 'a')" >> {
        StringUtils.containsNotPattern("abcde", "a") === false
      }
      "invalidate ('abcde', 'e')" >> {
        StringUtils.containsNotPattern("abcde", "e") === false
      }
      "invalidate ('abcde012345, '[a-z][0-9]')" >> {
        StringUtils.containsNotPattern("abcde012345", "[a-z][0-9]") === false
      }
      "validate ('abcde', 'f')" >> {
        StringUtils.containsNotPattern("abcde", "f") === true
      }
      "validate ('abcde', '[0-9]')" >> {
        StringUtils.containsNotPattern("abcde", "[0-9]") === true
      }
      "validate ('abcdef012345', '[0-9][a-z]')" >> {
        StringUtils.containsNotPattern("abcde012345", "[0-9][a-z]") === true
      }
    }

    "StringUtils.isSecure should" >> {
      "validate ('Abcdef012345-')" >> {
        StringUtils.isSecure("Abcdef012345-") === true
      }
      "validate ('Abcdef@012345')" >> {
        StringUtils.isSecure("Abcdef@012345") === true
      }
      "validate ('A@eiu90u+9u')" >> {
        StringUtils.isSecure("A@eiu90u+9u") === true
      }
      "invalidate ('Abcdef012345')" >> {
        StringUtils.isSecure("Abcdef012345") === false
      }
      "invalidate ('Abcdefghijk-')" >> {
        StringUtils.isSecure("Abcdefghijk-") === false
      }
      "invalidate ('abcdef012345-')" >> {
        StringUtils.isSecure("abcdef012345-") === false
      }
      "invalidate ('Ab0-')" >> {
        StringUtils.isSecure("Ab0-") === false
      }
      "validate ('abcdef012345', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isSecure("abcdef012345-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === true
      }
      "validate ('ABCDEF012345', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isSecure("ABCDEF012345-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === true
      }
      "invalidate ('abcdefghij', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isSecure("abcdefghij-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === false
      }
      "invalidate ('012345678', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isSecure("012345678-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === false
      }
    }

    "StringUtils.isNotSecure should" >> {
      "invalidate ('Abcdef012345-')" >> {
        StringUtils.isNotSecure("Abcdef012345-") === false
      }
      "invalidate ('Abcdef@012345')" >> {
        StringUtils.isNotSecure("Abcdef@012345") === false
      }
      "invalidate ('A@eiu90u+9u')" >> {
        StringUtils.isNotSecure("A@eiu90u+9u") === false
      }
      "validate ('Abcdef012345')" >> {
        StringUtils.isNotSecure("Abcdef012345") === true
      }
      "validate ('Abcdefghijk-')" >> {
        StringUtils.isNotSecure("Abcdefghijk-") === true
      }
      "validate ('abcdef012345-')" >> {
        StringUtils.isNotSecure("abcdef012345-") === true
      }
      "validate ('Ab0-')" >> {
        StringUtils.isNotSecure("Ab0-") === true
      }
      "invalidate ('abcdef012345', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isNotSecure("abcdef012345-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === false
      }
      "invalidate ('ABCDEF012345', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isNotSecure("ABCDEF012345-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === false
      }
      "validate ('abcdefghij', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isNotSecure("abcdefghij-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === true
      }
      "validate ('012345678', !needUppercaseLetter, !needLowercaseLetter, !needSymbol)" >> {
        StringUtils.isNotSecure("012345678-", needUppercaseLetter = false, needLowercaseLetter = false, needSymbol = false) === true
      }
    }

    "StringUtils.generateSalt should" >> {
      "generate a n-length string for n = 10" >> {
        StringUtils.generateSalt(10) must have size 10
      }
      "generate a n-length string for n = 25" >> {
        StringUtils.generateSalt(25) must have size 25
      }
      "contain only alphanumeric characters" >> {
        StringUtils.generateSalt(1000) must be matching "^[a-zA-Z0-9]+$"
      }
      "generate two different string for two consecutive calls" >> {
        StringUtils.generateSalt(10) !== StringUtils.generateSalt(10)
      }
    }

    "StringUtils.crypt should" >> {
      "generate the same string for same parameters" >> {
        StringUtils.crypt("abcdef", "ghijk") === StringUtils.crypt("abcdef", "ghijk")
      }
      "generate different string for different parameters" >> {
        StringUtils.crypt("abcdef", "ghijk") !== StringUtils.crypt("lmnopq", "rstuvw")
      }
    }

    "StringUtils.isCanonical should" >> {
      "validate 'abcd'" >> {
        StringUtils.isCanonical("abcd") === true
      }

      "validate '1234'" >> {
        StringUtils.isCanonical("1234") === true
      }

      "validate 'abcd-+_1234'" >> {
        StringUtils.isCanonical("abcd-+_1234") === true
      }

      "validate 'a1-ea49-ier_u'" >> {
        StringUtils.isCanonical("a1-ea49-ier_u") === true
      }

      "invalidate 'ab cd'" >> {
        StringUtils.isCanonical("ab cd") === false
      }

      "invalidate '-abcd'" >> {
        StringUtils.isCanonical("-abcd") === false
      }

      "invalidate 'abcd+'" >> {
        StringUtils.isCanonical("abcd+") === false
      }

      "invalidate '1234_'" >> {
        StringUtils.isCanonical("1234_") === false
      }

      "invalidate 'ABCD'" >> {
        StringUtils.isCanonical("ABCD") === false
      }

      "invalidate 'aBcd'" >> {
        StringUtils.isCanonical("aBcd") === false
      }
    }

    "StringUtils.isNotCanonical should" >> {
      "invalidate 'abcd'" >> {
        StringUtils.isNotCanonical("abcd") === false
      }

      "invalidate '1234'" >> {
        StringUtils.isNotCanonical("1234") === false
      }

      "invalidate 'abcd-+_1234'" >> {
        StringUtils.isNotCanonical("abcd-+_1234") === false
      }

      "invalidate 'a1-ea49-ier_u'" >> {
        StringUtils.isNotCanonical("a1-ea49-ier_u") === false
      }

      "validate 'ab cd'" >> {
        StringUtils.isNotCanonical("ab cd") === true
      }

      "validate '-abcd'" >> {
        StringUtils.isNotCanonical("-abcd") === true
      }

      "validate 'abcd+'" >> {
        StringUtils.isNotCanonical("abcd+") === true
      }

      "validate '1234_'" >> {
        StringUtils.isNotCanonical("1234_") === true
      }

      "validate 'ABCD'" >> {
        StringUtils.isNotCanonical("ABCD") === true
      }

      "validate 'aBcd'" >> {
        StringUtils.isNotCanonical("aBcd") === true
      }
    }

    "StringUtils.canonicalize should" >> {
      "keep 'abcd' as 'abcd'" >> {
        StringUtils.canonicalize("abcd") === "abcd"
      }

      "transform 'some words here' to 'some-words-here'" >> {
        StringUtils.canonicalize("some words here") === "some-words-here"
      }

      "transform 'Some UppErCase' to 'some-uppercase'" >> {
        StringUtils.canonicalize("Some UppErCase") === "some-uppercase"
      }

      "transform 'S0m€ spéc!al Chàràctèrs hërê' to 's0me-spec-al-characters-here'" >> {
        StringUtils.canonicalize("S0m€ spéc!al Chàràctèrs hërê") === "s0me-spec-al-characters-here"
      }
    }
  }
}
