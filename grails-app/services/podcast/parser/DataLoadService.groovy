package podcast.parser

import be.ceau.itunesapi.Lookup
import be.ceau.itunesapi.request.Entity
import be.ceau.itunesapi.response.Response
import com.opencsv.CSVReader
import grails.gorm.transactions.Transactional
import podcast.parser.model.CsvWrapper
import podcast.parser.utils.MiscUtils

import java.util.stream.Collectors

@Transactional
class DataLoadService {

  def podcastService


  def serviceMethod() {}

  def readCsv(path) {

    def list = new ArrayList()

    def reader = new FileReader(new File(path))
    def csvReader = new CSVReader(reader)

    String[] line
    while ((line = csvReader.readNext()) != null) {
      def item = new CsvWrapper().builder(line)
      list.add(item)
    }
    reader.close()
    csvReader.close()
    return list
  }


  def getBatchPodcast(def arrIds) {
    Response response = new Lookup()
        .setIds(arrIds)
        .setEntity(Entity.PODCAST)
        .execute();

    return response.getResults();
  }


  def freshCsvParser() {
    String splitPath = '/splits/'
    String filePrefix = 'FileNumber_'
    String fileExtension = '.csv'

    def allThreads = []
    for (int i = 0; i <= 10; i++) {
      def records = readCsv(splitPath + filePrefix + i + fileExtension);
      log.debug("{}", records);
      def arrayChunk = MiscUtils.partition(records, 100)
      log.debug("{}", arrayChunk);
      arrayChunk.eachWithIndex { arr, int j ->
        allThreads << Thread.start {
          def arrIds = arr?.parallelStream()?.map { it.itunesId }?.collect(Collectors.toList())
          def results = getBatchPodcast(arrIds);
          results?.parallelStream()?.forEach { entry ->
            podcastService.savePodcastFinal(entry)
          }
        }
      }
    }

    allThreads.each { it.join() }

    // TODO delete files and folders and update and delete products
    new File(splitPath).deleteDir()
//    if (fileToProgress.exists()) {
//      fileToProgress.delete()
//    }
  }

  def trendingCsvParser() {
    String splitPath = '/splits/'
    String filePrefix = 'FileNumber_'
    String fileExtension = '.csv'

    def allThreads = []
    for (int i = 0; i <= 10; i++) {
      def records = readCsv(splitPath + filePrefix + i + fileExtension);
      log.debug("{}", records);
      def arrayChunk = MiscUtils.partition(records, 100)
      log.debug("{}", arrayChunk);
      arrayChunk.eachWithIndex { arr, int j ->
        allThreads << Thread.start {
          def arrIds = arr?.parallelStream()?.map { it.itunesId }?.collect(Collectors.toList())
          def results = getBatchPodcast(arrIds);
          results?.parallelStream()?.forEach { entry ->
            podcastService.savePodcastFinal(entry)
          }
        }
      }
    }

    allThreads.each { it.join() }

    // TODO delete files and folders and update and delete products
    new File(splitPath).deleteDir()
//    if (fileToProgress.exists()) {
//      fileToProgress.delete()
//    }
  }
}
