package com.jworkflow.providers.mongodb;

import com.google.inject.Provider;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoPersistenceServiceProvider implements Provider<MongoPersistenceService>{

    String uri;
    
    public MongoPersistenceServiceProvider(String uri) {
        this.uri = uri;
    }
    
    @Override
    public MongoPersistenceService get() {
        try {
            return new MongoPersistenceService(uri);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MongoPersistenceServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static MongoPersistenceServiceProvider configure(String uri) {
        return new MongoPersistenceServiceProvider(uri);
    }
    
}
