package com.arghyam.commons.di

import com.arghyam.example.ui.ExampleActivity
import com.arghyam.iam.ui.LoginActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(loginActivity: LoginActivity)

    fun inject(exampleActivity: ExampleActivity)
}
