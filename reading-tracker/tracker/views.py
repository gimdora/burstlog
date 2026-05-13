"""
Views for the reading tracker.
book_list shows all books. book_detail shows one book and its notes.
"""

from django.shortcuts import render, get_object_or_404, redirect

from .models import Book, Note


def book_list(request):
    """Shows the list of all books and the add book form."""

    if request.method == "POST":
        # User submitted the add book form
        title = request.POST.get("title", "").strip()
        author = request.POST.get("author", "").strip()
        status = request.POST.get("status", "want")

        # Only save if title and author were filled in
        if title and author:
            Book.objects.create(
                title=title,
                author=author,
                status=status,
            )
            # Redirect after POST so a page refresh does not save it again
            return redirect("book_list")

    books = Book.objects.all()
    context = {
        "books": books,
        "status_choices": Book.STATUS_CHOICES,
    }
    return render(request, "tracker/book_list.html", context)


def book_detail(request, book_id):
    """Shows one book and its notes, plus the add note form."""
    # Return 404 if a book with this id does not exist
    book = get_object_or_404(Book, pk=book_id)

    if request.method == "POST":
        # User submitted the add note form
        body = request.POST.get("body", "").strip()
        if body:
            Note.objects.create(book=book, body=body)
            return redirect("book_detail", book_id=book.id)

    # book.notes works because of related_name="notes" in the Note model.
    # Notes are already sorted newest first by the Meta.ordering on Note.
    notes = book.notes.all()

    context = {
        "book": book,
        "notes": notes,
    }
    return render(request, "tracker/book_detail.html", context)
