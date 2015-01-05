package renderdiff.service.comparator;

/**
 *
 */
public interface PageQueueInterface {
    /**
     * Add url to list.
     *
     * @param url URL to add
     */
    public void addUrl(String url);

    /**
     * Iterate to next url
     */
    public void iterate();
}
