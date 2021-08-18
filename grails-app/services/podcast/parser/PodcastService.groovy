package podcast.parser

import be.ceau.itunesapi.response.Result
import grails.gorm.transactions.Transactional

@Transactional
class PodcastService {

  def sessionFactory

  def serviceMethod() {}

  def savePodcastFinal(Result result) {
    def podcast = new Podcast();
    podcast.collectionId = result?.getCollectionId()
    podcast.collectionName = result?.getCollectionName()
    podcast.description = result?.getCollectionId()
    podcast.collectionViewUrl = result?.getCollectionViewUrl()
    podcast.artistName = result?.getArtistName()
    podcast.artistViewUrl = result?.getArtistViewUrl()
    podcast.wrapperType = result?.getWrapperType()
    podcast.kind = result?.getKind()
    podcast.feedUrl = result?.getFeedUrl()
    podcast.previewUrl = result?.getPreviewUrl()
    podcast.artworkUrl30 = result?.getArtworkUrl30()
    podcast.artworkUrl60 = result?.getArtworkUrl60()
    podcast.artworkUrl100 = result?.getArtworkUrl100()
    podcast.artworkUrl512 = result?.getArtworkUrl512()
    podcast.artworkUrl600 = result?.getArtworkUrl600()
    podcast.releaseDate = result?.getReleaseDate()
    podcast.trackCount = result?.getTrackCount()
    podcast.country = result?.getCountry()
    podcast.copyright = result?.getCopyright()
    podcast.shortDescription = result?.getShortDescription()
    podcast.longDescription = result?.getLongDescription()

    Podcast.withTransaction { status ->
      try {
        URL url = new URL(result.getFeedUrl())
        com.icosillion.podengine.models.Podcast podcastData = new com.icosillion.podengine.models.Podcast(url)

        podcast.description = podcastData.getDescription() ?: ""
        def episodes = podcastData.getEpisodes()

        podcast.episodeCount = episodes.size()
        podcast.lastEpisodeDate = episodes.get(0).getPubDate()


        if (podcast.save(flush: true, failOnError: true)) {
          result?.getGenres()?.parallelStream()?.forEach { name ->
            new Genres(name: name, podcast: podcast).save(flush: true)
          }



          episodes?.parallelStream()
              ?.forEach { entry ->
                def episode = new Episode()
                episode.title = entry?.getTitle() ?: ""
                episode.description = entry?.getDescription() ?: ""
                episode.guid = entry?.getGUID()
                episode.hostedUrl = entry?.getLink()
                episode.pubDate = entry?.getPubDate()
                episode.durationString = entry?.getITunesInfo()?.getDuration() ?: null
                episode.durationString = entry?.getITunesInfo()?.getDuration() ?: null
                episode.link = entry?.getEnclosure()?.getURL()
                episode.duration = entry?.getEnclosure()?.getLength()
                episode.type = entry?.getEnclosure()?.getType()
                episode.podcast = podcast
                if (episode.save(flush: true, failOnError: true)) {
                  log.debug("Successfully!! Episode Saved!!")
                } else {
                  status.setRollbackOnly()
                  log.debug("Failed!! Episode Saved!!")
                }
              }
        } else {
          status.setRollbackOnly()
        }
      } catch (Exception e) {
        log.warn("[Episode] Exception::", e)
        status.setRollbackOnly()
      }
    }
    sessionFactory.currentSession.clear()
  }


  def updatePodcastFinal(Long itunesId) {
    def podcast = Podcast.findByCollectionId(itunesId)
    if (podcast) {
      try {
        com.icosillion.podengine.models.Podcast podcastData = new com.icosillion.podengine.models.Podcast(podcast.feedUrl)

        def episodes = podcastData.getEpisodes()

        episodes.each { entry ->
          Date oldPubDate = podcast.lastEpisodeDate;
          Date newPubDate = entry?.getPubDate()

          if (oldPubDate.compareTo(newPubDate)) {
            def episode = new Episode()
            episode.title = entry?.getTitle() ?: ""
            episode.description = entry?.getDescription() ?: ""
            episode.guid = entry?.getGUID()
            episode.hostedUrl = entry?.getLink()
            episode.pubDate = entry?.getPubDate()
            episode.durationString = entry?.getITunesInfo()?.getDuration() ?: null
            episode.durationString = entry?.getITunesInfo()?.getDuration() ?: null
            episode.link = entry?.getEnclosure()?.getURL()
            episode.duration = entry?.getEnclosure()?.getLength()
            episode.type = entry?.getEnclosure()?.getType()
            episode.podcast = podcast
            episode.save(flush: true, failOnError: true)
          } else {
            return
          }
        }

        podcast.lastEpisodeDate = episodes.get(0).getPubDate()
        podcast.save(flush: true, failOnError: true)

      } catch (Exception e) {
        log.warn("[Episode] Exception::", e)
      }
    }
    sessionFactory.currentSession.clear()
  }


}
