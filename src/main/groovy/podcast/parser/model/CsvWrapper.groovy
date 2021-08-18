package podcast.parser.model

class CsvWrapper {
  String id
  String url
  String itunesId
  String originalUrl
  String newestItemPubdate
  String oldestItemPubdate
  String language

  CsvWrapper builder(obj){
    if(obj.size() != 7){
      return null
    }

    this.id = obj[0]
    this.url = obj[1]
    this.itunesId = obj[2]
    this.originalUrl = obj[3]
    this.newestItemPubdate = obj[4]
    this.oldestItemPubdate = obj[5]
    this.language = obj[6]

    return this
  }
}
