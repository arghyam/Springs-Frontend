package com.arghyam.commons.di

import com.arghyam.additionalDetails.ui.AddAdditionalDetailsActivity
import com.arghyam.addspring.ui.NewSpringActivity
import com.arghyam.admin.ui.AdminPanelActivity
import com.arghyam.example.ui.ExampleActivity
import com.arghyam.favourites.ui.FavouritesFragment
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.iam.ui.OtpVerifyActivity
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.more.ui.MoreFragment
import com.arghyam.myactivity.ui.MyActivityFragment
import com.arghyam.notification.ui.activity.DisplayDischargeDataActivity
import com.arghyam.notification.ui.activity.NotificationActivity
import com.arghyam.profile.ui.ProfileActivity
import com.arghyam.search.ui.SearchFragment
import com.arghyam.splash.ui.SplashActivity
import com.arghyam.springdetails.ui.activity.AddDischargeActivity
import com.arghyam.springdetails.ui.activity.SpringDetailsActivity
import com.arghyam.springdetails.ui.fragments.DetailsFragment
import com.arghyam.springdetails.ui.fragments.DischargeDataFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(loginActivity: LoginActivity)

    fun inject(exampleActivity: ExampleActivity)

    fun inject(otpVerifyActivity: OtpVerifyActivity)

    fun inject(profileActivity: ProfileActivity)

    fun inject(addDischargeActivity: AddDischargeActivity)

    fun inject(newSpringActivity: NewSpringActivity)

    fun inject(addAdditionalDetailsActivity: AddAdditionalDetailsActivity)

    fun inject(favouritesFragment: FavouritesFragment)

    fun inject(homeFragment: HomeFragment)

    fun inject(detailsFragment: DetailsFragment)

    fun inject(springDetailsActivity: SpringDetailsActivity)

    fun inject(dischargeDataFragment: DischargeDataFragment)

    fun inject(moreFragment: MoreFragment)

    fun inject(adminPanelActivity: AdminPanelActivity)

    fun inject(myActivityFragment: MyActivityFragment)

    fun inject (searchFragment: SearchFragment)

    fun inject(notificationActivity: NotificationActivity)

    fun inject(displayDischargeDataActivity: DisplayDischargeDataActivity)

    fun inject(splashActivity: SplashActivity)

}
