package podcast.parser

import be.ceau.itunesapi.response.Result
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import podcast.parser.model.GenreData

@Transactional
class PodcastService {

  def sessionFactory

  def serviceMethod() {}

  def savePodcastFinal(Result result) {
    def podcast = new Podcast()
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

    Podcast.withNewTransaction { status ->
      if (result.getFeedUrl()) {
        try {
          URL url = new URL(result.getFeedUrl())
          com.icosillion.podengine.models.Podcast podcastData = new com.icosillion.podengine.models.Podcast(url)

          podcast.description = podcastData.getDescription() ?: ""
          def episodes = podcastData.getEpisodes()

          podcast.episodeCount = episodes.size()
          podcast.lastEpisodeDate = episodes.get(0).getPubDate()

          if (podcast.save(flush: true, failOnError: true)) {
            GenreData[] genreData = new GenreData[result.getGenreIds().size()];
            int i = 0;
            for (String id : result.getGenreIds()) {
              genreData[i] = new GenreData();
              genreData[i].id = Long.parseLong(id);
              i++;
            }

            i = 0;
            for (String name : result.getGenres()) {
              genreData[i].name = name;
              i++;
            }

            for (GenreData genre : genreData) {
              if (genre.id != 26 || !genre.name.equals("Podcasts")) {
                def findGenre = Genres.findByName(genre.name)
                if (findGenre) {
                  findGenre.addToPodcasts(podcasts: podcast)
                } else {
                  new Genres(name: genre.name).save(flush: true).addToPodcasts(podcasts: podcast)
                }
              }
            }

            episodes?.parallelStream()
                ?.forEach { entry ->

                  def episode = new Episode()
                  try {
                    episode.title = entry?.getTitle() ?: ""
                  } catch (Exception e) {
                    log.error("Unable to get Episode title", e)
                  }

                  try {
                    episode.description = entry?.getDescription() ?: ""
                  } catch (Exception e) {
                    log.warn("Unable to get Episode description", e)
                  }

                  try {
                    episode.guid = entry?.getGUID()
                  } catch (Exception e) {
                    log.warn("Unable to get Episode GUID", e)
                  }

                  try {
                    episode.hostedUrl = entry?.getLink()
                  } catch (Exception e) {
                    log.warn("Unable to get Episode Hosted URL", e)
                  }

                  try {
                    episode.pubDate = entry?.getPubDate()
                  } catch (Exception e) {
                    log.error("Unable to get Episode Hosted PubDate", e)
                  }

                  try {
                    episode.durationString = entry?.getITunesInfo()?.getDuration() ?: null
                  } catch (Exception e) {
                    log.warn("Unable to get Episode Duration!!", e)
                  }

                  try {
                    episode.link = entry?.getEnclosure()?.getURL()
                  } catch (Exception e) {
                    log.error("Unable to get Episode URL", e)
                  }

                  try {
                    episode.duration = entry?.getEnclosure()?.getLength()
                  } catch (Exception e) {
                    log.warn("Unable to get Episode Duration!!", e)
                  }

                  try {
                    episode.type = entry?.getEnclosure()?.getType()
                  } catch (Exception e) {
                    log.warn("Unable to get Episode Type!!", e)
                  }

                  episode.podcast = podcast
                  if (episode.save(flush: true, failOnError: true)) {
                    log.debug("Successfully!! Episode Saved!!")
                  } else {
                    status.setRollbackOnly()
                    log.error("Failed!! Episode Saved!!")
                  }
                }
          } else {
            status.setRollbackOnly()
          }
        } catch (Exception e) {
          log.error("Unable to Parse URL:: ", e)
          status.setRollbackOnly()
        }
      } else {
        log.error("Feed URL:: ", result?.getFeedUrl())
        status.setRollbackOnly()
      }

    }
    sessionFactory.currentSession.clear()
    return true
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

        podcast.episodeCount = episodes.size()
        podcast.lastEpisodeDate = episodes.get(0).getPubDate()
        podcast.save(flush: true, failOnError: true)

      } catch (Exception e) {
        log.error("[Episode] Exception::", e)
      }
    }
    sessionFactory.currentSession.clear()
  }
}
