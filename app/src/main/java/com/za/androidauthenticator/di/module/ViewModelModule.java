package com.za.androidauthenticator.di.module;

import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.viewmodel.AuthenticatorViewModel;
import com.za.androidauthenticator.viewmodel.DetailCodeViewModel;
import com.za.androidauthenticator.viewmodel.EnterKeyViewModel;
import com.za.androidauthenticator.viewmodel.ScanQrViewModel;
import com.za.androidauthenticator.viewmodel.ViewModelFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.inject.Provider;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module
public class ViewModelModule {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Provides
    ViewModelFactory viewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(AuthenticatorViewModel.class)
    ViewModel authenticatorViewModel(UserRepository userRepository) {
        return new AuthenticatorViewModel(userRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(DetailCodeViewModel.class)
    ViewModel detailCodeViewModel(UserRepository userRepository) {
        return new DetailCodeViewModel(userRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EnterKeyViewModel.class)
    ViewModel enterKeyViewModel(UserRepository userRepository) {
        return new EnterKeyViewModel(userRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScanQrViewModel.class)
    ViewModel scanQrViewModel() {
        return new ScanQrViewModel();
    }
}
