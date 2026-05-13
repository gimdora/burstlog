"""App config for the tracker app (mostly the default from Django)."""

from django.apps import AppConfig


class TrackerConfig(AppConfig):
    """Just keep the default settings that Django created."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "tracker"
