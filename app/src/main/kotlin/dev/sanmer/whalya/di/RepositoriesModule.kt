package dev.sanmer.whalya.di

import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.repository.ClientRepositoryImpl
import dev.sanmer.whalya.repository.DbRepository
import dev.sanmer.whalya.repository.DbRepositoryImpl
import dev.sanmer.whalya.repository.LicensesRepository
import dev.sanmer.whalya.repository.LicensesRepositoryImpl
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.repository.RemoteRepositoryImpl
import org.koin.dsl.module

val repositories = module {
    single<DbRepository> {
        DbRepositoryImpl(get())
    }

    single<ClientRepository> {
        ClientRepositoryImpl()
    }

    factory {
        get<ClientRepository>().current
    }

    single<RemoteRepository> {
        RemoteRepositoryImpl { get() }
    }

    single<LicensesRepository> {
        LicensesRepositoryImpl(get())
    }
}