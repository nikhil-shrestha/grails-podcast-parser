package podcast.parser


class CsvParserService {

  def splitLargeFile(lines, files){
    String inputfile = "file.csv";
    BufferedReader br = new BufferedReader(new FileReader(inputfile))

    String splitPath = 'splits/'
    String filePrefix = 'FileNumber_'
    String fileExtension = '.csv'

    new File(splitPath).mkdir()

    String strLine = null;
    for (int i = 0; i < files; i++) {
      def fstream1 = new FileWriter(splitPath + filePrefix + i + fileExtension); //creating a new file writer.
      def out = new BufferedWriter(fstream1);
      for (int j = 0; j < lines; j++) {   //iterating the reader to read only the first few lines of the csv as defined earlier
        strLine = br.readLine()
        if (strLine != null) {
          def strar = strLine.split(",")
          if (!strar[0].equals("id")) {     // removing heading row of the csv file
            out.write(strLine)
            out.newLine()
          }
        }
      }
      out.close()
    }
  }

  def processInputFile(directory = '') {
    int lines = 100  //set this to whatever number of lines you need in each file
    int count = 0
    String inputfile = "file.csv"
    File file = new File(inputfile)
    if (!file.exists()) {
      log.warn "File does not exist"
    } else {
      Scanner scanner = new Scanner(file)
      while (scanner.hasNextLine()) {  //counting the lines in the input file
        scanner.nextLine()
        count++
      }
      int files = 0
      if ((count % lines) == 0) {
        files = (count / lines) as int
      } else {
        files = ((count / lines) + 1) as int
      }
      splitLargeFile(lines, files);
    }
  }
}
