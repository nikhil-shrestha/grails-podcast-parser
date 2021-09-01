package podcast.parser

class Podcast {

  String wrapperType
  String kind
  Long collectionId
  String artistName
  String collectionName
  String artistViewUrl
  String collectionViewUrl
  String feedUrl
  String previewUrl
  String artworkUrl30
  String artworkUrl60
  String artworkUrl100
  String artworkUrl512
  String artworkUrl600
  String releaseDate
  Integer trackCount
  String copyright
  String country
  String shortDescription
  String longDescription
  String description
  String currentVersionReleaseDate
  Integer episodeCount

  Date lastEpisodeDate

  List<Genres> genres
  static hasMany = [genres: Genres, episodes: Episode]

  static constraints = {
    wrapperType nullable: true
    kind nullable: true
    collectionId nullable: false, unique: true
    artistName nullable: true
    collectionName nullable: false
    artistViewUrl nullable: true
    collectionViewUrl nullable: true
    feedUrl nullable: true
    previewUrl nullable: true
    artworkUrl30 nullable: true
    artworkUrl60 nullable: true
    artworkUrl100 nullable: true
    artworkUrl512 nullable: true
    artworkUrl600 nullable: true
    releaseDate nullable: true
    trackCount nullable: true
    copyright nullable: true
    country nullable: true
    shortDescription nullable: true
    longDescription nullable: true
    description nullable: true
    currentVersionReleaseDate nullable: true
    episodeCount nullable: true
    lastEpisodeDate nullable: false
  }

  static mapping = {
    description type: 'text'
    shortDescription type: 'text'
    longDescription type: 'text'
  }
}
