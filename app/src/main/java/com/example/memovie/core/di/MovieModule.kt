package com.example.memovie.core.di

import com.example.memovie.core.data.repository.MovieRepositoryImpl
import com.example.memovie.core.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class MovieModule {

    @Binds
    @Singleton
    abstract fun provideMovieRepository(movieRepo: MovieRepositoryImpl): MovieRepository

}