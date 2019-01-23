package org.ordo.project.model;

public interface RestInterface {

    public ReplyMessage retrieveMatchingEntites(Entity entityTempate);
    
    public ReplyMessage retrieveEntity(Integer id);
    
}
