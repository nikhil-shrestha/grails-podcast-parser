package podcast.parser.utils

class MiscUtils {

  def static partition(array, size) {
    def partitions = []
    int partitionCount = array.size() / size

    partitionCount.times { partitionNumber ->
      def start = partitionNumber * size
      def end = start + size - 1
      partitions << array[start..end]
    }

    if (array.size() % size) partitions << array[partitionCount * size..-1]
    return partitions
  }
}
