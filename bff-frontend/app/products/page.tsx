"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ProductDialog } from "@/components/product-dialog"
import { apiCall, getCurrentUser, logout } from "@/lib/api-client"
import { LogOut, Plus, User, RefreshCw } from "lucide-react"

interface Product {
  id: string
  name: string
  price: number
  createdAt: string
}

interface UserInfo {
  username?: string
  email?: string
  name?: string
  sub?: string
}

export default function ProductsPage() {
  const router = useRouter()
  const [user, setUser] = useState<UserInfo | null>(null)
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [dialogOpen, setDialogOpen] = useState(false)

  useEffect(() => {
    loadUserAndProducts()
  }, [])

  const loadUserAndProducts = async () => {
    setLoading(true)
    
    // Get current user info from Gateway (session-based)
    const userResult = await getCurrentUser()
    if (userResult.success && userResult.data) {
      setUser(userResult.data)
      // Load products after confirming authentication
      await loadProducts()
    } else {
      // Not authenticated, redirect to login
      router.push("/login")
    }
    
    setLoading(false)
  }

  const loadProducts = async () => {
    const result = await apiCall<Product[]>("/products")

    if (result.success && result.data) {
      setProducts(result.data)
    } else if (result.error?.includes("401")) {
      // Session expired, redirect to login
      router.push("/login")
    }
  }

  const handleRefresh = async () => {
    setRefreshing(true)
    await loadProducts()
    setRefreshing(false)
  }

  const handleCreateProduct = async (name: string, price: string) => {
    const result = await apiCall<Product>("/products", {
      method: "POST",
      body: JSON.stringify({ name, price: parseFloat(price) }),
    })

    if (result.success && result.data) {
      setProducts([result.data, ...products])
      setDialogOpen(false)
    } else if (result.error?.includes("401")) {
      router.push("/login")
    } else {
      alert(result.error || "Failed to create product")
    }
  }

  const handleLogout = async () => {
    await logout() // This will redirect after logout
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center space-y-4">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="text-muted-foreground">Loading your products...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Navigation */}
      <nav className="sticky top-0 z-50 bg-background/80 backdrop-blur-md border-b border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <h1 className="text-2xl font-bold">Product Manager</h1>
            <span className="text-xs px-2 py-1 rounded-full bg-primary/10 text-primary border border-primary/20">
              🔒 BFF Pattern
            </span>
          </div>
          <div className="flex items-center gap-4">
            {user && (
              <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-muted">
                <User className="h-4 w-4 text-muted-foreground" />
                <span className="text-sm font-medium">
                  {user.name || user.username || user.email || 'User'}
                </span>
              </div>
            )}
            <Button
              variant="outline"
              size="sm"
              onClick={handleLogout}
              className="flex items-center gap-2"
            >
              <LogOut className="h-4 w-4" />
              Logout
            </Button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-3xl font-bold mb-2">Your Products</h2>
            <p className="text-muted-foreground">
              Securely managed via API Gateway with session-based auth
            </p>
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={handleRefresh}
              disabled={refreshing}
              className="flex items-center gap-2"
            >
              <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
              Refresh
            </Button>
            <Button
              onClick={() => setDialogOpen(true)}
              className="bg-primary hover:bg-primary/90 flex items-center gap-2"
            >
              <Plus className="h-4 w-4" />
              Create Product
            </Button>
          </div>
        </div>

        {/* Products Grid */}
        {products.length === 0 ? (
          <Card className="border-dashed">
            <CardContent className="flex flex-col items-center justify-center py-12">
              <p className="text-lg font-semibold mb-2">No products yet</p>
              <p className="text-muted-foreground text-center max-w-sm mb-6">
                Click the "Create Product" button to add your first product
              </p>
              <Button 
                onClick={() => setDialogOpen(true)} 
                className="bg-primary hover:bg-primary/90"
              >
                Create Your First Product
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {products.map((product) => (
              <Card key={product.id} className="flex flex-col hover:shadow-lg transition-shadow">
                <CardHeader>
                  <CardTitle className="line-clamp-2">{product.name}</CardTitle>
                  <CardDescription>
                    {new Date(product.createdAt).toLocaleDateString()}
                  </CardDescription>
                </CardHeader>
                <CardContent className="flex-1">
                  <div className="flex items-baseline gap-2">
                    <span className="text-3xl font-bold text-primary">
                      ${product.price.toFixed(2)}
                    </span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </main>

      {/* Product Dialog */}
      <ProductDialog 
        open={dialogOpen} 
        onOpenChange={setDialogOpen} 
        onSubmit={handleCreateProduct} 
      />
    </div>
  )
}