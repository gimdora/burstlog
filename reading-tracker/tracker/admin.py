"""
Register Book and Note with Django admin so I can see the data at /admin/.
"""

from django.contrib import admin

from .models import Book, Note


@admin.register(Book)
class BookAdmin(admin.ModelAdmin):
    """How the Book list looks in the admin page."""
    list_display = ("title", "author", "status", "created_at")
    list_filter = ("status",)
    search_fields = ("title", "author")


@admin.register(Note)
class NoteAdmin(admin.ModelAdmin):
    """Same idea for Note."""
    list_display = ("book", "created_at")
    list_filter = ("book",)
