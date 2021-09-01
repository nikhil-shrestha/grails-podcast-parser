package podcast.parser

class Genres {

    String name

    static hasMany = [podcasts: Podcast]

    static constraints = {
        name nullable: false, unique: true
    }
}
