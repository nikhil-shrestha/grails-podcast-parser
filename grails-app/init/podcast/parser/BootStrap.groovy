package podcast.parser

class BootStrap {

  def dataLoadService
  def csvParserService

  def init = { servletContext ->
    if (Podcast.count() == 0) {
//      csvParserService.processInputFile()
      dataLoadService.freshCsvParser()
    }
//    dataLoadService.trendingCsvParser()
  }

  def destroy = {
  }
}
