import torch
import torch.nn as nn
import numpy as np
import matplotlib.pyplot as plt

# Définition du réseau
class Modele(torch.nn.Module):
    def __init__(self):
        super().__init__()
        self.l1 = nn.Linear(2, 3)  # 2 entrées, 3 neurones
        self.l2 = nn.Linear(3, 1)  # 3 neurones, 1 sortie

    def forward(self, x):
        x = torch.heaviside(self.l1(x), torch.tensor(1.0))
        x = torch.heaviside(self.l2(x), torch.tensor(1.0))
        return x

modele = Modele()

# Poids couche 1
modele.l1.weight.data = torch.tensor([
    [-1., 2.],   # neurone 1
    [ 2.,-1.],   # neurone 2
    [-1.,-1.]    # neurone 3
])
modele.l1.bias.data = torch.tensor([-1., -2., 5.])

# Poids couche 2 (AND à 3 entrées)
modele.l2.weight.data = torch.tensor([[1., 1., 1.]])
modele.l2.bias.data = torch.tensor([-2.])

# Grille de points
x1 = np.linspace(-4, 4, 300)
x2 = np.linspace(-4, 4, 300)
X1, X2 = np.meshgrid(x1, x2)
grid = torch.tensor(
    np.stack([X1.ravel(), X2.ravel()], axis=1),
    dtype=torch.float32
)

# Prédiction
with torch.no_grad():
    Z = modele(grid).numpy().reshape(X1.shape)

# Affichage
plt.figure(figsize=(6, 6))
plt.contourf(X1, X2, Z, levels=[-0.5, 0.5, 1.5], colors=['white', 'red'], alpha=0.4)
plt.contour(X1, X2, Z, levels=[0.5], colors='black')
plt.axhline(0, color='k', linewidth=0.5)
plt.axvline(0, color='k', linewidth=0.5)
plt.xlabel('x1')
plt.ylabel('x2')
plt.title('Activation map')
plt.grid(True)
plt.show()