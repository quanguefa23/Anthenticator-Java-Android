package com.za.androidauthenticator.di.module;

import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.viewmodel.AuthenticatorViewModel;
import com.za.androidauthenticator.viewmodel.DetailCodeViewModel;
import com.za.androidauthenticator.viewmodel.EnterKeyViewModel;
import com.za.androidauthenticator.viewmodel.ScanQrViewModel;
import com.za.androidauthenticator.viewmodel.ExportCodeViewModel;
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
    ViewModel authenticatorViewModel(AuthRepository authRepository) {
        return new AuthenticatorViewModel(authRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(DetailCodeViewModel.class)
    ViewModel detailCodeViewModel(AuthRepository authRepository) {
        return new DetailCodeViewModel(authRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EnterKeyViewModel.class)
    ViewModel enterKeyViewModel(AuthRepository authRepository) {
        return new EnterKeyViewModel(authRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScanQrViewModel.class)
    ViewModel scanQrViewModel(AuthRepository authRepository) {
        return new ScanQrViewModel(authRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ExportCodeViewModel.class)
    ViewModel exportCodeViewModel() {
        return new ExportCodeViewModel();
    }
}
