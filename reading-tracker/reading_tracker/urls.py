"""
Main URLs for the project.
Sends / to /books/ and includes the tracker app URLs.
"""

from django.contrib import admin
from django.urls import path, include
from django.views.generic import RedirectView


urlpatterns = [
    path("admin/", admin.site.urls),
    # If the user opens "/", send them to the books list
    path("", RedirectView.as_view(url="/books/", permanent=False)),
    # All the tracker app URLs (the actual /books/ pages)
    path("", include("tracker.urls")),
]
