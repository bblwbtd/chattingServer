package utils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TrashBin {
    public static CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static void drop(Disposable disposable){
        compositeDisposable.add(disposable);
    }

}
