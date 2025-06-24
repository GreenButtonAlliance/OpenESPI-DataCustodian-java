/*
 * Green Button Data Custodian Application JavaScript
 * Copyright (c) 2018-2025 Green Button Alliance, Inc.
 */

// Wait for DOM to be ready
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize data tables
    initializeDataTables();
    
    // Initialize modals
    initializeModals();
    
    // Initialize CSRF token for AJAX requests
    initializeCSRF();
});

/**
 * Initialize Bootstrap tooltips
 */
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Initialize form validation
 */
function initializeFormValidation() {
    // Bootstrap form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Custom validation for specific forms
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', validateLoginForm);
    }
}

/**
 * Initialize data tables with sorting and filtering
 */
function initializeDataTables() {
    const tables = document.querySelectorAll('.data-table');
    tables.forEach(function(table) {
        // Add sorting functionality
        const headers = table.querySelectorAll('th[data-sort]');
        headers.forEach(function(header) {
            header.style.cursor = 'pointer';
            header.addEventListener('click', function() {
                sortTable(table, header.cellIndex, header.dataset.sort);
            });
        });
        
        // Add search functionality if search input exists
        const searchInput = document.querySelector(`[data-table="${table.id}"]`);
        if (searchInput) {
            searchInput.addEventListener('input', function() {
                filterTable(table, this.value);
            });
        }
    });
}

/**
 * Initialize modal dialogs
 */
function initializeModals() {
    // Confirmation modals
    const confirmButtons = document.querySelectorAll('[data-confirm]');
    confirmButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            const message = this.dataset.confirm;
            if (!confirm(message)) {
                event.preventDefault();
                return false;
            }
        });
    });
    
    // Dynamic modal loading
    const modalTriggers = document.querySelectorAll('[data-bs-toggle="modal"][data-url]');
    modalTriggers.forEach(function(trigger) {
        trigger.addEventListener('click', function() {
            loadModalContent(this.dataset.url, this.dataset.bsTarget);
        });
    });
}

/**
 * Initialize CSRF token for AJAX requests
 */
function initializeCSRF() {
    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
    
    if (csrfToken && csrfHeader) {
        // Set up CSRF token for all AJAX requests
        const token = csrfToken.getAttribute('content');
        const header = csrfHeader.getAttribute('content');
        
        // jQuery setup (if available)
        if (typeof $ !== 'undefined') {
            $.ajaxSetup({
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(header, token);
                }
            });
        }
        
        // Fetch API setup
        const originalFetch = window.fetch;
        window.fetch = function(url, options = {}) {
            if (!options.headers) {
                options.headers = {};
            }
            options.headers[header] = token;
            return originalFetch(url, options);
        };
    }
}

/**
 * Validate login form
 */
function validateLoginForm(event) {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        event.preventDefault();
        showAlert('Please enter both username and password.', 'danger');
        return false;
    }
    
    return true;
}

/**
 * Sort table by column
 */
function sortTable(table, columnIndex, sortType) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    const isAscending = table.dataset.sortDirection !== 'asc';
    table.dataset.sortDirection = isAscending ? 'asc' : 'desc';
    
    rows.sort(function(a, b) {
        const aValue = a.children[columnIndex].textContent.trim();
        const bValue = b.children[columnIndex].textContent.trim();
        
        let comparison = 0;
        
        if (sortType === 'number') {
            comparison = parseFloat(aValue) - parseFloat(bValue);
        } else if (sortType === 'date') {
            comparison = new Date(aValue) - new Date(bValue);
        } else {
            comparison = aValue.localeCompare(bValue);
        }
        
        return isAscending ? comparison : -comparison;
    });
    
    // Clear tbody and append sorted rows
    tbody.innerHTML = '';
    rows.forEach(row => tbody.appendChild(row));
    
    // Update sort indicators
    table.querySelectorAll('th').forEach(th => th.classList.remove('sort-asc', 'sort-desc'));
    table.querySelectorAll('th')[columnIndex].classList.add(isAscending ? 'sort-asc' : 'sort-desc');
}

/**
 * Filter table based on search term
 */
function filterTable(table, searchTerm) {
    const rows = table.querySelectorAll('tbody tr');
    const term = searchTerm.toLowerCase();
    
    rows.forEach(function(row) {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(term) ? '' : 'none';
    });
}

/**
 * Load modal content via AJAX
 */
function loadModalContent(url, modalSelector) {
    const modal = document.querySelector(modalSelector);
    if (!modal) return;
    
    const modalBody = modal.querySelector('.modal-body');
    
    // Show loading spinner
    modalBody.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"></div></div>';
    
    fetch(url)
        .then(response => response.text())
        .then(html => {
            modalBody.innerHTML = html;
            // Re-initialize any components in the loaded content
            initializeFormValidation();
        })
        .catch(error => {
            modalBody.innerHTML = '<div class="alert alert-danger">Error loading content</div>';
            console.error('Error loading modal content:', error);
        });
}

/**
 * Show alert message
 */
function showAlert(message, type = 'info', duration = 5000) {
    const alertContainer = document.getElementById('alert-container') || createAlertContainer();
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    alertContainer.appendChild(alertDiv);
    
    // Auto-dismiss after duration
    if (duration > 0) {
        setTimeout(() => {
            const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
            alert.close();
        }, duration);
    }
}

/**
 * Create alert container if it doesn't exist
 */
function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alert-container';
    container.className = 'position-fixed top-0 end-0 p-3';
    container.style.zIndex = '1055';
    document.body.appendChild(container);
    return container;
}

/**
 * Format numbers with proper locale
 */
function formatNumber(number, locale = 'en-US') {
    return new Intl.NumberFormat(locale).format(number);
}

/**
 * Format dates
 */
function formatDate(date, options = {}) {
    const defaultOptions = {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    };
    return new Intl.DateTimeFormat('en-US', { ...defaultOptions, ...options }).format(new Date(date));
}

/**
 * Debounce function for search inputs
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showAlert('Copied to clipboard!', 'success', 2000);
    } catch (err) {
        console.error('Failed to copy: ', err);
        showAlert('Failed to copy to clipboard', 'danger', 3000);
    }
}

/**
 * Download file from URL
 */
function downloadFile(url, filename) {
    const link = document.createElement('a');
    link.href = url;
    link.download = filename || '';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// Export functions for use in other scripts
window.GreenButton = {
    showAlert,
    formatNumber,
    formatDate,
    copyToClipboard,
    downloadFile,
    debounce
};