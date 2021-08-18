package podcast.parser


class CsvParserService {

  def processInputFile(directory = '') {
    String inputfile = "file.csv";
    def dataFile = new File(inputfile);
    String splitPath = directory + '/splits/'
    String filePrefix = 'FileNumber_'
    String fileExtension = '.csv'
    int i
    int fileNumber
    if (!dataFile.exists()) {
      log.debug "File does not exist"
    } else {
      i = 0
      fileNumber = 0
      new File(splitPath).mkdir()
      File fileToWrite = new File(splitPath + filePrefix + fileNumber + fileExtension)
      dataFile.eachLine { line ->
        if (i > 10000) {
          i = 0
          fileNumber += 1
          fileToWrite = new File(splitPath + filePrefix + fileNumber + fileExtension)
        }
        i = i + 1
        fileToWrite << ("$line\r\n")
      }
    }
  }
}
