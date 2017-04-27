package io.github.jistol.remote.exception;

/**
 * Created by kimjh on 2017-04-27.
 */
public class RemoteException extends RuntimeException {
    public RemoteException(String message){
        super(message);
    }

    public RemoteException(Throwable e){
        super(e);
    }
}
