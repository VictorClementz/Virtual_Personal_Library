import React, { useState, useEffect } from 'react';
import { Search, BookOpen, Plus, X, Library, Tag, User, Heart } from 'lucide-react';
import './App.css';

const App = () => {
  // State management
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [personalLibrary, setPersonalLibrary] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [activeTab, setActiveTab] = useState('search');
  const [notification, setNotification] = useState('');

  // Load personal library on component mount
  useEffect(() => {
    loadPersonalLibrary();
  }, []);

  // API calls to your backend
  const searchBooks = async (query) => {
    if (!query.trim()) return;

    setIsSearching(true);
    try {
      // Adjust this URL based on your backend server port (usually 8080)
      const response = await fetch(`http://localhost:8080/books/search-json?query=${encodeURIComponent(query)}`);


      if (!response.ok) {
        throw new Error('Search failed');
      }

      // Since your current endpoint returns HTML (Thymeleaf), we need to modify this
      // For now, let's assume you'll create a JSON endpoint
      const book = await response.json();

      if (book && book.title) {
        setSearchResults([book]);
      } else {
        setSearchResults([]);
      }
    } catch (error) {
      console.error('Search failed:', error);
      setSearchResults([]);
      showNotification('Search failed. Please try again.');
    }
    setIsSearching(false);
  };

  const addToLibrary = async (book) => {
    try {
      const response = await fetch('http://localhost:8080/books', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(book),
      });

      if (response.ok) {
        const savedBook = await response.json();
        setPersonalLibrary(prev => [...prev, savedBook]);
        showNotification('Book added to your library!');
      } else {
        throw new Error('Failed to add book');
      }
    } catch (error) {
      console.error('Failed to add book:', error);
      showNotification('Failed to add book to library');
    }
  };

  const removeFromLibrary = async (bookId) => {
    try {
      const response = await fetch(`http://localhost:8080/books/${bookId}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        setPersonalLibrary(prev => prev.filter(book => book.id !== bookId));
        showNotification('Book removed from library');
      } else {
        throw new Error('Failed to remove book');
      }
    } catch (error) {
      console.error('Failed to remove book:', error);
      showNotification('Failed to remove book from library');
    }
  };

  const loadPersonalLibrary = async () => {
    try {
      const response = await fetch('http://localhost:8080/books');

      if (!response.ok) {
        throw new Error('Failed to load library');
      }

      const books = await response.json();
      setPersonalLibrary(books);
    } catch (error) {
      console.error('Failed to load library:', error);
      showNotification('Failed to load your library');
    }
  };

  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => setNotification(''), 3000);
  };

  const handleSearch = (e) => {
    if (e && e.preventDefault) e.preventDefault();
    searchBooks(searchQuery);
  };

  const isBookInLibrary = (book) => {
    return personalLibrary.some(libBook =>
      libBook.title === book.title && libBook.authors === book.authors
    );
  };

  // Components
  const BookCard = ({ book, isInLibrary = false, onAdd, onRemove }) => (
    <div className="book-card">
      <div className="book-card-content">
        <div className="book-thumbnail">
          {book.thumbnailUrl ? (
            <img
              src={book.thumbnailUrl}
              alt={book.title}
              className="thumbnail-image"
            />
          ) : (
            <div className="thumbnail-placeholder">
              <BookOpen size={32} />
            </div>
          )}
        </div>

        <div className="book-details">
          <h3 className="book-title">
            {book.title || 'Unknown Title'}
          </h3>

          <div className="book-info">
            <User size={16} />
            <span>{book.authors || 'Unknown Author'}</span>
          </div>

          {book.category && (
            <div className="book-info">
              <Tag size={16} />
              <span>{book.category}</span>
            </div>
          )}

          <div className="book-actions">
            {isInLibrary ? (
              <button
                onClick={() => onRemove && onRemove(book.id)}
                className="btn btn-remove"
              >
                <X size={16} />
                Remove
              </button>
            ) : (
              <button
                onClick={() => onAdd && onAdd(book)}
                disabled={isBookInLibrary(book)}
                className={`btn ${isBookInLibrary(book) ? 'btn-disabled' : 'btn-add'}`}
              >
                <Plus size={16} />
                {isBookInLibrary(book) ? 'Already Added' : 'Add to Library'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );

  const SearchTab = () => (
    <div className="tab-content">
      <div className="search-form">
        <div className="search-input-container">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch(e)}
            placeholder="Search by book title or ISBN..."
            className="search-input"
          />
        </div>
        <button
          onClick={handleSearch}
          disabled={isSearching}
          className="btn btn-search"
        >
          {isSearching ? 'Searching...' : 'Search'}
        </button>
      </div>

      {searchResults.length > 0 && (
        <div className="results-section">
          <h2>Search Results</h2>
          {searchResults.map((book, index) => (
            <BookCard
              key={`search-${index}`}
              book={book}
              onAdd={addToLibrary}
            />
          ))}
        </div>
      )}

      {searchQuery && searchResults.length === 0 && !isSearching && (
        <div className="empty-state">
          <BookOpen size={48} />
          <p>No books found for "{searchQuery}"</p>
          <p className="empty-state-subtitle">Try a different search term or ISBN</p>
        </div>
      )}
    </div>
  );

  const LibraryTab = () => (
    <div className="tab-content">
      <div className="library-header">
        <h2>
          <Library size={28} />
          My Personal Library
        </h2>
        <div className="book-count">
          {personalLibrary.length} books
        </div>
      </div>

      {personalLibrary.length > 0 ? (
        <div className="results-section">
          {personalLibrary.map((book) => (
            <BookCard
              key={book.id}
              book={book}
              isInLibrary={true}
              onRemove={removeFromLibrary}
            />
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <Heart size={48} />
          <p>Your library is empty</p>
          <p className="empty-state-subtitle">Start by searching for books to add to your collection</p>
          <button
            onClick={() => setActiveTab('search')}
            className="btn btn-search"
          >
            Search for Books
          </button>
        </div>
      )}
    </div>
  );

  return (
    <div className="app">
      {/* Header */}
      <header className="header">
        <div className="header-content">
          <div className="logo">
            <BookOpen size={32} />
            <h1>Virtual Library</h1>
          </div>

          {/* Tab Navigation */}
          <nav className="tab-nav">
            <button
              onClick={() => setActiveTab('search')}
              className={`tab-btn ${activeTab === 'search' ? 'active' : ''}`}
            >
              <Search size={16} />
              Search
            </button>
            <button
              onClick={() => setActiveTab('library')}
              className={`tab-btn ${activeTab === 'library' ? 'active' : ''}`}
            >
              <Library size={16} />
              My Library ({personalLibrary.length})
            </button>
          </nav>
        </div>
      </header>

      {/* Main Content */}
      <main className="main-content">
        {activeTab === 'search' ? <SearchTab /> : <LibraryTab />}
      </main>

      {/* Notification */}
      {notification && (
        <div className="notification">
          {notification}
        </div>
      )}
    </div>
  );
};

export default App;