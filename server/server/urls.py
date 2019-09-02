"""server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from newsForApp import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path("helloworld/", views.index),
    path("signIn/", views.userSignIn),
    path("signUp/", views.userSignUp),
    path("signOut/", views.userSignOut),
    path("history/", views.history),
    path("collection/", views.collection),
    path("weightMap/", views.weightMap),
    path("getAllNews/", views.getAllNews),
    path("filterWordsMap/", views.filterWords),
    path("userMessage", views.userMessage),
    path("userForwardingNews", views.forwardingNews),
    path("deleteNewsAndMessage", views.deleteNewsAndMessage),
]
