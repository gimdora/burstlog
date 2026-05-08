def greet(name: str) -> str:
    """Return a personalized greeting."""
    return f"Hello, {name}! Welcome to my software portfolio."


def main() -> None:
    print("=" * 50)
    print(greet("World"))
    print("=" * 50)

    modules = ["Web Apps", "Mobile App", "Cloud Databases"]
    print("\nModules I plan to complete this semester:")
    for index, module in enumerate(modules, start=1):
        print(f"  Module #{index}: {module}")

    print("\nReady to build something great!")


if __name__ == "__main__":
    main()
