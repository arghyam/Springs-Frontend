package com.arghyam.commons.di

import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.example.ui.ExampleActivity
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.iam.ui.OtpVerifyActivity
import com.arghyam.profile.ui.ProfileActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(loginActivity: LoginActivity)

    fun inject(exampleActivity: ExampleActivity)

    fun inject(otpVerifyActivity: OtpVerifyActivity)

    fun inject(profileActivity: ProfileActivity)

    fun inject(newSpringActivity: NewSpringActivity)

}
