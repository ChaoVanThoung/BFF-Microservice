"use client"

import type React from "react"
import { useState } from "react"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { AlertCircle } from "lucide-react"

interface ProductDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (title: string, price: string) => void
}

export function ProductDialog({ open, onOpenChange, onSubmit }: ProductDialogProps) {
  const [title, setTitle] = useState("")
  const [price, setPrice] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    if (!title.trim()) {
      setError("Product title is required")
      return
    }

    if (!price.trim() || isNaN(Number(price)) || Number(price) <= 0) {
      setError("Price must be a valid positive number")
      return
    }

    setLoading(true)
    onSubmit(title.trim(), price.trim())
    setTitle("")
    setPrice("")
    setError("")
    setLoading(false)
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Create New Product</DialogTitle>
          <DialogDescription>Add a new product to your inventory</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">Product Title</label>
            <Input
              type="text"
              placeholder="Enter product title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              disabled={loading}
              className="h-10"
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium">Price</label>
            <Input
              type="number"
              placeholder="Enter price"
              step="0.01"
              min="0"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              disabled={loading}
              className="h-10"
            />
          </div>

          {error && (
            <div className="flex gap-2 p-3 rounded-lg bg-red-50 border border-red-200 dark:bg-red-950 dark:border-red-800">
              <AlertCircle className="h-4 w-4 text-red-600 dark:text-red-400 mt-0.5 flex-shrink-0" />
              <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
            </div>
          )}

          <div className="flex gap-3 justify-end pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={loading}>
              Cancel
            </Button>
            <Button type="submit" className="bg-primary hover:bg-primary/90" disabled={loading}>
              {loading ? "Creating..." : "Create Product"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}