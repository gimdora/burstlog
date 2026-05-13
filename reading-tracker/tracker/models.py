"""
Models for the reading tracker app.
Book is the main table. Note connects to Book by a foreign key.
"""

from django.db import models


class Book(models.Model):
    """One book that the user wants to read or has read."""

    # Status options for the dropdown on the add book form.
    # Left side is what gets saved, right side is what the user sees.
    STATUS_CHOICES = [
        ("want", "Want to read"),
        ("reading", "Currently reading"),
        ("finished", "Finished"),
    ]

    title = models.CharField(max_length=200)
    author = models.CharField(max_length=120)
    status = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES,
        default="want",
    )
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        # Show newest books first by default
        ordering = ["-created_at"]

    def __str__(self):
        # Used by Django admin to show a readable name
        return f"{self.title} by {self.author}"

    def note_count(self):
        # How many notes does this book have right now
        return self.notes.count()


class Note(models.Model):
    """A short note attached to one book."""

    # CASCADE means if a book is deleted, its notes are deleted too.
    # related_name="notes" lets me write book.notes in the views.
    book = models.ForeignKey(
        Book,
        on_delete=models.CASCADE,
        related_name="notes",
    )
    body = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ["-created_at"]
        # Show newest notes first
    def __str__(self):
        # Short preview for the admin page
        preview = self.body[:40]
        return f"Note on {self.book.title}: {preview}"
