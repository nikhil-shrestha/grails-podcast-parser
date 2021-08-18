package podcast.parser

class Episode {

    String title
    String description
    String guid
    URL hostedUrl
    Date pubDate
    String episodeNumber
    String durationString
    Long duration
    URL link
    String type

    static belongsTo = [podcast: Podcast]

    static constraints = {
        title nullable: false
        description nullable: true
        guid nullable: true
        hostedUrl nullable: true
        pubDate nullable: false
        episodeNumber nullable: true
        durationString nullable: true
        duration nullable: true
        link nullable: false
        type nullable: true
    }

    static mapping = {
        description type: 'text'
    }
}
