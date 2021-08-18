package podcast.parser

import podcast.parser.utils.DateUtils
import spock.lang.Specification


class DateUtilsSpec  extends Specification {


  void "test string format yyyy-MM-dd'T'HH:mm:ss'Z'"(){
    def date = DateUtils.stringToDate(new Date().toString())

    expect:
    date instanceof Date
  }

}
