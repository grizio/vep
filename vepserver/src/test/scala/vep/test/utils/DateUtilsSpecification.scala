package vep.test.utils

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.utils.DateUtils

@RunWith(classOf[JUnitRunner])
class DateUtilsSpecification extends Specification {
  "Specifications of DateUtils functions" >> {
    "DateUtils.toDateTimeOpt should" >> {
      "returns a date from '2015-01-01' as 01 january 2015" >> {
        val date = DateUtils.toDateTimeOpt("2015-01-01")
        (date must beSome[DateTime]) and
          (date.get.getYear === 2015) and
          (date.get.getMonthOfYear === 1) and
          (date.get.getDayOfMonth === 1)
      }

      "returns a date from '2015-02-27T12:15:13' as 27 february 2015 at 12h 15m 13s" >> {
        val date = DateUtils.toDateTimeOpt("2015-02-27T12:15:13")
        (date must beSome[DateTime]) and
          (date.get.getYear === 2015) and
          (date.get.getMonthOfYear === 2) and
          (date.get.getDayOfMonth === 27) and
          (date.get.getHourOfDay === 12) and
          (date.get.getMinuteOfHour === 15) and
          (date.get.getSecondOfMinute === 13)
      }

      "returns a date from '2015-02-2712:15:13' as None" >> {
        val date = DateUtils.toDateTimeOpt("2015-02-212:15:13")
        date must beNone
      }
    }

    "DateUtils.toDateTime should" >> {
      "returns a date from '2015-01-01' as 01 january 2015" >> {
        val date = DateUtils.toDateTime("2015-01-01")
        (date.getYear === 2015) and
          (date.getMonthOfYear === 1) and
          (date.getDayOfMonth === 1)
      }

      "returns a date from '2015-02-27T12:15:13' as 27 february 2015 at 12h 15m 13s" >> {
        val date = DateUtils.toDateTime("2015-02-27T12:15:13")
        (date.getYear === 2015) and
          (date.getMonthOfYear === 2) and
          (date.getDayOfMonth === 27) and
          (date.getHourOfDay === 12) and
          (date.getMinuteOfHour === 15) and
          (date.getSecondOfMinute === 13)
      }

      "throw an exception" >> {
        {
          DateUtils.toDateTime("2015-02-212:15:13")
        } must throwAn[Exception]
      }
    }

    "DateUtils.isIsoDate should" >> {
      "returns true for '2015-01-01'" >> {
        DateUtils.isIsoDate("2015-01-01") === true
      }

      "returns true for '2015-02-27T12:15:13'" >> {
        DateUtils.isIsoDate("2015-02-27T12:15:13") === true
      }

      "returns false for '2015-02-2712:15:13'" >> {
        DateUtils.isIsoDate("2015-02-2712:15:13") === false
      }

      "returns false for '2015-02-30'" >> {
        DateUtils.isIsoDate("2015-02-30") === false
      }
    }

    "DateUtils.isNotIsoDate should" >> {
      "returns false for '2015-01-01'" >> {
        DateUtils.isNotIsoDate("2015-01-01") === false
      }

      "returns false for '2015-02-27T12:15:13'" >> {
        DateUtils.isNotIsoDate("2015-02-27T12:15:13") === false
      }

      "returns true for '2015-02-2712:15:13'" >> {
        DateUtils.isNotIsoDate("2015-02-2712:15:13") === true
      }

      "returns true for '2015-02-30'" >> {
        DateUtils.isNotIsoDate("2015-02-30") === true
      }
    }

    "DateUtils.toStringSQL should" >> {
      "returns '2015-01-01 00:00:00' for DateTime(2015-01-01)" >> {
        DateUtils.toStringSQL(DateUtils.toDateTime("2015-01-01")) === "2015-01-01 00:00:00"
      }

      "returns '2015-01-01 01:01:01' for DateTime(2015-01-01T01:01:01)" >> {
        DateUtils.toStringSQL(DateUtils.toDateTime("2015-01-01T01:01:01")) === "2015-01-01 01:01:01"
      }
    }

    "DateUtils.toStringISO should" >> {
      "returns '2015-01-01T00:00:00' for DateTime(2015-01-01)" >> {
        DateUtils.toStringISO(DateUtils.toDateTime("2015-01-01")) === "2015-01-01T00:00:00"
      }

      "returns '2015-01-01T01:01:01' for DateTime(2015-01-01T01:01:01)" >> {
        DateUtils.toStringISO(DateUtils.toDateTime("2015-01-01T01:01:01")) === "2015-01-01T01:01:01"
      }
    }
  }
}
