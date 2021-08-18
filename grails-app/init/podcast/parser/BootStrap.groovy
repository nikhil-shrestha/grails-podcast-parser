package podcast.parser

class BootStrap {

  def dataLoadService

  def init = { servletContext ->
    if (Podcast.count() == 0) {
      dataLoadService.freshCsvParser()
    }
//    dataLoadService.trendingCsvParser()
  }

  def destroy = {
  }
}
