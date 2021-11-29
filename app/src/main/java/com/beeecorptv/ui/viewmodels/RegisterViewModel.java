package com.beeecorptv.ui.viewmodels;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.beeecorptv.data.model.auth.Login;
import com.beeecorptv.data.remote.ErrorHandling;
import com.beeecorptv.data.repository.AuthRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.disposables.CompositeDisposable;



/**
 * ViewModel to cache, retrieve data for RegisterActivity
 *
 * @author Yobex.
 */
public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Inject
    RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;

    }



    // Handle Registration Process
    public LiveData<ErrorHandling<Login>> getRegister(String name,String email , String password) {
        return authRepository.getRegisterDetail(name,email, password);
    }





    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
