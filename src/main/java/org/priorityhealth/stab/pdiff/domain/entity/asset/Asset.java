package org.priorityhealth.stab.pdiff.domain.entity.asset;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.AbstractEntity;

/**
 * Crawl
 */
@DatabaseTable(tableName = "asset.assets")
public class Asset extends AbstractEntity {

    /**
     * Name
     */
    @DatabaseField(canBeNull = false)
    protected String name;

    /**
     * Domain
     */
    @DatabaseField(canBeNull = false)
    protected String domain;

    /**
     * Load Complete Script
     */
    @DatabaseField
    protected String loadCompleteScript;

    /**
     * Login Node Url
     */
    @DatabaseField
    protected String loginNodeUrl;

    /**
     * Node Urls
     */
    @ForeignCollectionField(eager = false)
    protected ForeignCollection<Node> nodes;

    @DatabaseField
    protected String loginServer;

    @DatabaseField
    protected String username;

    @DatabaseField
    protected String password;

    /**
     *
     * @return Crawl
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name Crawl Name
     * @return Crawl
     */
    public Asset setName(String name) {
        this.name = name;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Asset setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     *
     * @return Load Complete Script
     */
    public String getLoadCompleteScript() {
        return loadCompleteScript;
    }

    /**
     *
     * @param loadCompleteScript Load Complete Script
     * @return Crawl
     */
    public Asset setLoadCompleteScript(String loadCompleteScript) {
        this.loadCompleteScript = loadCompleteScript;
        return this;
    }

    /**
     *
     * @return Login Node URL
     */
    public String getLoginNodeUrl() {
        return loginNodeUrl;
    }

    /**
     *
     * @param loginNodeUrl Login Node Url
     * @return Crawl
     */
    public Asset setLoginNodeUrl(String loginNodeUrl) {
        this.loginNodeUrl = loginNodeUrl;
        return this;
    }

    /**
     *
     * @return Nodes
     */
    public ForeignCollection<Node> getNodes() {
        return nodes;
    }

    /**
     *
     * @param nodes Nodes
     * @return Crawl
     */
    public Asset setNodes(ForeignCollection<Node> nodes) {
        this.nodes = nodes;
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Asset addNode(Node node) {
        nodes.add(node);
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Asset removeNode(Node node) {
        nodes.remove(node);
        return this;
    }

    public String getLoginServer() {
        return loginServer;
    }

    public Asset setLoginServer(String loginServer) {
        this.loginServer = loginServer;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Asset setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Asset setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "{name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
