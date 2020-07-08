package com.za.androidauthenticator.di.module;

import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.viewmodels.AuthenticatorViewModel;
import com.za.androidauthenticator.viewmodels.ViewModelFactory;

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
}
