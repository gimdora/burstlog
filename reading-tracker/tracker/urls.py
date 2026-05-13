"""
URLs for the tracker app. Included from the project urls.py.
"""

from django.urls import path

from . import views


urlpatterns = [
    # The list page
    path("books/", views.book_list, name="book_list"),
    # The detail page. <int:book_id> is captured from the URL and passed to the view.
    path("books/<int:book_id>/", views.book_detail, name="book_detail"),
]
