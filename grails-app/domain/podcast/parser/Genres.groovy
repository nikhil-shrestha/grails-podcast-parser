package podcast.parser

class Genres {

    String name
    Podcast podcast

    static constraints = {
        name nullable: false, unique: true
    }
}
