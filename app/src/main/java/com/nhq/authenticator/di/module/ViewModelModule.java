package com.nhq.authenticator.di.module;

import androidx.lifecycle.ViewModel;

import com.nhq.authenticator.data.repository.Repository;
import com.nhq.authenticator.data.repository.SignInManager;
import com.nhq.authenticator.viewmodel.AuthenticatorViewModel;
import com.nhq.authenticator.viewmodel.DetailCodeViewModel;
import com.nhq.authenticator.viewmodel.EnterKeyViewModel;
import com.nhq.authenticator.viewmodel.ExportCodeViewModel;
import com.nhq.authenticator.viewmodel.ScanQrViewModel;
import com.nhq.authenticator.viewmodel.ViewModelFactory;

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
    ViewModel authenticatorViewModel(Repository repository, SignInManager signInManager) {
        return new AuthenticatorViewModel(repository, signInManager);
    }

    @Provides
    @IntoMap
    @ViewModelKey(DetailCodeViewModel.class)
    ViewModel detailCodeViewModel(Repository repository) {
        return new DetailCodeViewModel(repository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(EnterKeyViewModel.class)
    ViewModel enterKeyViewModel(Repository repository) {
        return new EnterKeyViewModel(repository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ScanQrViewModel.class)
    ViewModel scanQrViewModel(Repository repository) {
        return new ScanQrViewModel(repository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ExportCodeViewModel.class)
    ViewModel exportCodeViewModel() {
        return new ExportCodeViewModel();
    }
}
